<?php
/*
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


// http header
header("Content-Type: application/xhtml+xml; charset=UTF-8");


// remove escape chars if set automatically by php
if (get_magic_quotes_gpc())
{
    foreach ($_POST as $key => $value)
        $_POST[$key] = stripslashes($value);
    foreach ($_REQUEST as $key => $value)
        $_REQUEST[$key] = stripslashes($value);
}


// set if something goes wrong in the beginning
$starterror = false;


// Load the config
require('conf.php');



// authenticate
if (!isset($_SERVER['PHP_AUTH_USER'])
 || !isset($user[$_SERVER['PHP_AUTH_USER']])
 || $user[$_SERVER['PHP_AUTH_USER']] != $_SERVER['PHP_AUTH_PW'])
{
    header('WWW-Authenticate: Basic realm="'.$config['realm'].'"');
    header("HTTP/1.0 401 Unauthorized");
    $starterror = "user authentication failed";
}



// user authed?
if (!$starterror)
{
    // load the palava library;
    require($config['palavalib']);

    // load palava
    $palava = new Palava($config['palavaconf']);

    if ($palava->established()) {
        $palava->session_start();
    } else {
        $starterror = implode("<br />", $palava->errors());
    }
}

echo '<?xml version="1.0" encoding="UTF-8" ?>'; ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
       "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <title>palava console</title>

        <link rel="stylesheet" type="text/css" href="console.css" />
        <script src="console.js" type="text/javascript"></script>
    </head>

    <body>
        <div id="main">




            <div id="header">
                <h1>palava console</h1>
                <?php
                    if ($starterror)
                        echo '<h2 id="error">'.$starterror.'</h2>';
                    else
                    {
                        echo '<h2 id="version">palava backend version: '.$palava->getBackendVersion().'</h2>';
                        echo '<h2 id="session">sid: '.$palava->getSessionID().'</h2>';
                    }
                ?>
            </div>




            <?php if (!$starterror) { ?>


            <?php if (isset($_POST['sendbinfile']) || isset($_POST['sendjs']) || isset($_POST['sendjsfile']) || isset($_POST['sendraw'])) { ?>

            <div id="output">

                <pre style="background-color: white !important"><?php

                    if (isset($_POST['sendbinfile']))
                    {
                        $file = $_FILES['binfile']['tmp_name'];
                        if (file_exists($file))
                        {
                            print_r($palava->uploadfile($_POST['job'], $_FILES['binfile']['tmp_name']));
                        }
                    }

                    if (isset($_POST['sendjsfile']))
                    {
                        $file = $_FILES['jsfile']['tmp_name'];
                        if (file_exists($file))
                        {
                            $_POST['jscontent'] = file_get_contents($file);
                            $_POST['sendjs'] = true;
                        }
                    }

                    if (isset($_POST['sendjs']))
                    {
                        $result = $palava->call_text('@palava.system.console', $_POST['jscontent']);
                        $result = str_replace('%%RED+%%', '<span style="color: red; background-color: white !important">', $result);
                        $result = str_replace('%%+RED%%', '</span>', $result);
                        $result = str_replace('%%GREEN+%%', '<span style="color: green; background-color: white !important">', $result);
                        $result = str_replace('%%+GREEN%%', '</span>', $result);
                        $result = str_replace('%%GREY+%%', '<span style="color: #888888; background-color: white !important">', $result);
                        $result = str_replace('%%+GREY%%', '</span>', $result);

                        echo trim($result);
                    }


                    if (isset($_POST['sendraw']))
                    {
                        $data = "$_POST[protocol]://$_POST[job]/$_POST[sessionid]/($_POST[length])?$_POST[rawcontent]";
                        $palava->data_send($data);
                        $result = print_r($palava->receive_response(), true);
                        echo htmlspecialchars($result);
                    }

                ?></pre>

            </div>

            <?php } ?>
                    




            <div id="java">
                <h3>javascript insertions</h3>

                <?php
                    if (!isset($_POST['jscontent'])) $_POST['jscontent'] = '';
                ?>

                <form method="post">
                    <div class="protocolbox">
                        <h4>Header:</h4>
                        <p class="protocol">text://de.cosmocode.palava.jobs.system.console/<?php echo substr($palava->getSessionID(), 0, 2).'..'.substr($palava->getSessionID(), strlen($palava->getSessionID()) - 2); ?>/(<span id="jslength"><?php echo strlen($_POST['jscontent']) ?></span>)?</p>
                        <h4>Body:</h4>
                        <p class="protocol"><textarea id="jscontent" class="content" name="jscontent" onkeyup="javascript:count('jscontent', 'jslength');"><?php echo htmlspecialchars($_POST['jscontent']); ?></textarea></p>
                        <input class="reqsubmit" type="submit" name="sendjs" value="send" />
                        <input class="reqsubmit" type="button" value="request" onclick="javascript:insertText('jscontent', 'client = Client.openConnection(server);\nclient.startSession(session.getSessionID());\n\nclient.sendRequest(\'data\', \'de.cosmocode.palava.jobs.system.sleep\', \'random=1000\');\n\nclient.close();');" />
                        <input class="reqsubmit" type="button" value="shutdown" onclick="javascript:insertText('jscontent', 'server.shutdown();');" />
                    </div>
                </form>

                <br />
                
                <form method="post" enctype="multipart/form-data">
                    <div class="protocolbox">
                        <h4>Choose a javascript file:</h4>
                        <p class="protocol">
                            <input type="file" name="jsfile" />
                        </p>
                        <input class="reqsubmit" type="submit" name="sendjsfile" value="send" />
                    </div>
                </form>


            </div>



            <div id="raw">
                <h3>protocol modifications</h3>

                <?php
                    if (!isset($_POST['protocol'])) $_POST['protocol'] = 'data';
                    if (!isset($_POST['job'])) $_POST['job'] = '';
                    if (!isset($_POST['sessionid'])) $_POST['sessionid'] = $palava->getSessionID();
                    if (!isset($_POST['length'])) $_POST['length'] = 0;
                    if (!isset($_POST['rawcontent'])) $_POST['rawcontent'] = '';
                ?>

                <form method="post">
                    <div class="protocolbox">
                        <h4>Header:</h4>
                        <p class="protocol"><select class="type" name="protocol"><?php foreach($config['palavatypes'] as $type) { echo "<option"; if ($type == $_POST['protocol']) echo " selected=\"selected\""; echo ">$type</option>"; } ?></select>://<input class="job" type="text" name="job" size="30" value="<?php echo htmlspecialchars($_POST['job']); ?>" />/<input class="sessionid" type="text" name="sessionid" size="10" value="<?php echo htmlspecialchars($_POST['sessionid']); ?>" />/(<input id="rawlength" class="length" type="text" name="length" size="5" value="<?php echo htmlspecialchars($_POST['length']) ?>" />)?</p>
                        <h4>Body:</h4>
                        <p class="protocol"><textarea id="rawcontent" class="content" name="rawcontent" onkeyup="javascript:count('rawcontent', 'rawlength');"><?php echo htmlspecialchars($_POST['rawcontent']) ?></textarea></p>
                        <input class="reqsubmit" type="submit" name="sendraw" value="send"/>
                    </div>
                </form>

                <br />
                
                <form method="post" enctype="multipart/form-data">
                    <div class="protocolbox">
                        <h4>Header:</h4>
                        <p class="protocol">binary://<input class="job" type="text" name="job" size="30" value="<?php echo htmlspecialchars($_POST['job']); ?>" />/<input class="sessionid" type="text" name="sessionid" size="10" value="<?php echo htmlspecialchars($_POST['sessionid']); ?>" />/(XXXXXX)?</p>
                        <h4>Choose a binary file:</h4>
                        <p class="protocol">
                            <input type="file" name="binfile" />
                        </p>
                        <input class="reqsubmit" type="submit" name="sendbinfile" value="send" />
                    </div>
                </form>


            </div>



            <?php } ?>




            <div id="footer">
                <a href="http://palava.cosmocode.de/" target="_blank">palava</a>, Copyright &copy; 2007, 2008 CosmoCode GmbH<br />
                palava comes with ABSOLUTELY NO WARRANTY; for details<br />
                see the LICENSE file.  This is free software, and you are<br />
                welcome to redistribute it under certain conditions.
            </div>
        </div>
    </body>
</html>
<?php


if (!$starterror)
{
    $palava->close();
}

?>
