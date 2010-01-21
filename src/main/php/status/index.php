<?php
/*
palava - a java-php-bridge
Copyright (C) 2008  CosmoCode GmbH

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

global $config;
$config['NAME'] = 'palava status monitor';
$config['file'] = $_SERVER['QUERY_STRING'];
$config['LIBRARY'] = '../Palava.php';
$config['machines'] = array();
$machines = array();

if (!$config['file']) {
    $config['file'] = 'monitor.xml';
} else {
    $config['file'] = str_replace('..', '_', $config['file']);  // disable back paths the ugly way
    $config['file'] .= '.xml';
}

global $error;
$error = array();

global $errname;
$errname = 0;

// remove escape chars if set automatically by php
if (get_magic_quotes_gpc()) {
    foreach ($_REQUEST as $key => $value) {
        $_REQUEST[$key] = stripslashes($value);
    }
}


function parseStartElement($parser, $name, $attributes)
{
    global $config, $_current_machine, $_current_palava, $_current_action, $_current_palavadefault, $errname;
    
    if (!isset($attributes['ENABLED'])) {
        $attributes['ENABLED'] = "true";
    }
    
    switch($name) {
        case 'MONITOR':
                $config = array_merge($config, $attributes);
                $config['machines'] = array();
                $config['machinedefault'] = array();
                $config['machinedefault']['actions'] = array();
                $config['machinedefault']['palavadefault'] = array();
                $config['machinedefault']['palavadefault']['actions'] = array();
                $_current_machine = null;
                $_current_palava = null;
                break;
                
        case 'MACHINEDEFAULT':
                $config['machinedefault'] = array_merge($config['machinedefault'], $attributes);
                break;

        case 'MACHINE':
                $mname = $attributes['NAME'];
                if ($mname == "") {
                    $mname = "machine".$errname++;
                    $error[] = "A machine has no name; using '$mname'";
                }
                if (isset($config['machines'][$mname])) {
                    $mname .= $errname++;
                    $error[] = "A machine has duplicate entries; using '$mname' for new entry";
                }
                $config['machines'][$mname] = $config['machinedefault'];
                $config['machines'][$mname] = array_merge($config['machines'][$mname], $attributes);
                $config['machines'][$mname]['palavas'] = array();
                $_current_machine = $mname;
                break;
                
        case 'PALAVADEFAULT':
                if (!$_current_machine) {
                    // global default
                    $config['machinedefault']['palavadefault'] = array_merge($config['machinedefault']['palavadefault'], $attributes);
                } else {
                    // machine default
                    $config['machines'][$_current_machine]['palavadefault'] = $config['machinedefault']['palavadefault'];
                    $config['machines'][$_current_machine]['palavadefault'] = array_merge($config['machines'][$_current_machine]['palavadefault'], $attributes);
                }
                $_current_palavadefault = true;
                break;
                
        case 'PALAVA':
                $pname = $attributes['NAME'];
                if ($pname == "") {
                    $pname = "palava".$errname++;
                    $error[] = "A palava has no name; using '$pname'";
                }
                if (isset($config['machines'][$pname])) {
                    $pname .= $errname++;
                    $error[] = "A palava has duplicate entries; using '$pname' for new entry";
                }
                $config['machines'][$_current_machine]['palavas'][$pname] = $config['machines'][$_current_machine]['palavadefault'];
                $config['machines'][$_current_machine]['palavas'][$pname] = array_merge($config['machines'][$_current_machine]['palavas'][$pname], $attributes);
                $_current_palava = $pname;
                break;
                
        default:
                $aname = $name;
                // machine default action
                if ($_current_machine == null && $_current_palava == null && !$_current_palavadefault) {    
                    $config['machinedefault']['actions'][$aname] = $attributes;
                    
                // machine default, palava default action
                } elseif ($_current_machine == null && $_current_palava == null && $_current_palavadefault) {
                    $config['machinedefault']['palavadefault']['actions'][$aname] = $attributes;
                    
                // specific machine action
                } elseif ($_current_machine != null && $_current_palava == null && !$_current_palavadefault) {
                    if (isset($config['machinedefault']['actions'][$aname])) {
                        $action = array_merge($config['machinedefault']['actions'][$aname], $attributes);
                    } else {
                        $action = $attributes;
                    }
                    $config['machines'][$_current_machine]['actions'][$aname] = $action;
                    
                // specific machine, palava default action
                } elseif ($_current_machine != null && $_current_palava == null && $_current_palavadefault) {
                    if (isset($config['machinedefault']['palavadefault']['actions'][$aname])) {
                        $action = array_merge($config['machinedefault']['palavadefault']['actions'][$aname], $attributes);
                    } else {
                        $action = $attributes;
                    }
                    $config['machines'][$_current_machine]['palavadefault']['actions'][$aname] = $action;
                
                // specific machine, specific palava action    
                } elseif ($_current_machine != null && $_current_palava != null) {
                    if (isset($config['machines'][$_current_machine]['palavadefault']['actions'][$aname])) {
                        $action = array_merge($config['machines'][$_current_machine]['palavadefault']['actions'][$aname], $attributes);
                    } else {
                        $action = $attributes;
                    }
                    $config['machines'][$_current_machine]['palavas'][$_current_palava]['actions'][$aname] = $action;
                } else {
                    $error[] = "unknown action state";
                    echo "ERROR";
                }
                $_current_action = $aname;
                break;
    }
}

function parseEndElement($parser, $name)
{
    global $config, $_current_machine, $_current_palava, $_current_action, $_current_palavadefault;
    switch($name) {
        case 'MACHINE': 
                $_current_machine = null;
                break;
        case 'PALAVA':
                $_current_palava = null;
                break;
        case 'PALAVADEFAULT':
                $_current_palavadefault = false;
                break;
        default:
                $_current_action = null;
    }
}

function parseCharacterData($parser, $content)
{
    global $config, $_current_machine, $_current_palava, $_current_action, $_current_palavadefault;
    // fill in command to execute
    
    // machine default action
    if ($_current_action) {
        if ($_current_machine == null && $_current_palava == null && !$_current_palavadefault) {
            $config['machinedefault']['actions'][$_current_action]['command'] = $content;
            
        // machine default, palava default action
        } elseif ($_current_machine == null && $_current_palava == null && $_current_palavadefault) {
            $config['machinedefault']['palavadefault']['actions'][$_current_action]['command'] = $content;
            
        // specific machine action
        } elseif ($_current_machine != null && $_current_palava == null && !$_current_palavadefault) {
            $config['machines'][$_current_machine]['actions'][$_current_action]['command'] = $content;
            
        // specific machine, palava default action
        } elseif ($_current_machine != null && $_current_palava == null && $_current_palavadefault) {
            $config['machines'][$_current_machine]['palavadefault']['actions'][$_current_action]['command'] = $content;
        
        // specific machine, specific palava action    
        } elseif ($_current_machine != null && $_current_palava != null) {
            $config['machines'][$_current_machine]['palavas'][$_current_palava]['actions'][$_current_action]['command'] = $content;
        }
    }
}

function runJS($palava, $js)
{
    $js = trim($js);
    $result = trim($palava->call_text('de.cosmocode.palava.jobs.system.console', $js));
    
    // ugly stripping result
    $ok = "%%GREY+%%Script successful executed.%%+GREY%%";
    
    if (substr($result, strlen($result) - strlen($ok)) == $ok) {
        $result = substr($result, 0, strlen($result) - strlen($ok));
        return trim($result);
    } else {
        return false;
    }
}

function runShell($palava, $command)
{
    $command = trim($command);
//    $command = escapeshellcmd($command);
    $command = str_replace('"', '\"', $command);
    $js = "
    
        process = java.lang.Runtime.getRuntime().exec(\"$command\");
        output = process.getInputStream();
        reader = new java.io.BufferedReader(new java.io.InputStreamReader(output));
        while ((line = reader.readLine()) != null) {
            out.write(line + \"\\n\");
        }
    
    ";
    
    return runJS($palava, $js);
}

function formatResult($result, $results)
{
    if(!isset($results[$result])) {
        return '<td>&nbsp;</td>';
    } else {
        $result = $results[$result];
        if (!isset($result['attributes']['ALIGN'])) {
            $result['attributes']['ALIGN'] = "left";
        }
        if (isset($result['attributes']['ROUND'])) {
            $result['value'] = number_format($result['value'], $result['attributes']['ROUND'], '.', ' ');
        }
        if (isset($result['attributes']['UNIT'])) {
            $result['value'] .= ' '.$result['attributes']['UNIT'];
        }
        return '<td class="'.$result['attributes']['ALIGN'].' '.$result['status'].'">'.$result['value'].'</td>';
    }
}



// parse config
global $_current_machine, $_current_palava, $_current_action, $_current_palavadefault;

if (!is_readable($config['file'])) {
    $error[] = 'Cannot read configuration file "'.$config['file'].'"';
} else {
    $xml_parser = xml_parser_create();
    xml_set_element_handler($xml_parser, "parseStartElement", "parseEndElement");
    xml_set_character_data_handler($xml_parser, "parseCharacterData");

    $fp = fopen($config['file'], 'r');
    while ($data = fread($fp, 4096)) {
        if (!xml_parse($xml_parser, $data, feof($fp))) {
            $error[] = sprintf('Error on parsing configuration file "'.$config['file'].'": %s in line %d',
                xml_error_string(xml_get_error_code($xml_parser)),
                xml_get_current_line_number($xml_parser));
            $config['machines'] = array();
                break;
        }
    }
    fclose($fp);
    xml_parser_free($xml_parser);
}

// use parsed configuration
$config['file'] = realpath($config['file']);
if ($config['LIBRARY']) {
    $config['LIBRARY'] = realpath($config['LIBRARY']);
} else {
    $config['LIBRARY'] = '<NOT SET>';
}


// load the library
if (!is_readable($config['LIBRARY'])) {
    $error[] = 'Library "'.$config['LIBRARY'].'" is not readable!';
} else {
    include($config['LIBRARY']);
    
    // now execute the main task of the tool:
    // inspect all palavas
    foreach($config['machines'] as $machine_name => $machine_config) {
    
        $machine = array();
        $machine['name'] = $machine_name;
        $machine['host'] = $machine_config['HOST'];
        $machine['results'] = array();
        $machine['palavas'] = array();
        
        foreach($machine_config['palavas'] as $palava_name => $palava_config) {
        
            $palava = array();
            $palava['name'] = $palava_name;
            $palava['port'] = $palava_config['PORT'];
            $palava['results'] = array();
            
            // test palava
            $palavaobj = new Palava($machine['host'].':'.$palava['port']);
            
            if (!$palavaobj->established()) {
                $palava['status'] = false;
            } else {
                $palava['status'] = true;
                
                // get the backends version
                $palava['version'] = $palavaobj->getBackendVersion();
                
                
                if (!isset($machine['system_fetched'])) {
                    // get all requested machine data
                    foreach($machine_config['actions'] as $aname => $action) {
                        if ($action['ENABLED'] != "true") {
                            continue;
                        }
                        if ($action['TYPE'] == "js") {
                            $result = array('name' => $aname, 'value' => runJS($palavaobj, $action['command']));
                        } elseif ($action['TYPE'] == "shell") {
                            $result = array('name' => $aname, 'value' => runShell($palavaobj, $action['command']));
                        }
                        $machine['results'][$aname] = $result;
                    }
                    
                    $machine['system_fetched'] = true;
                }
                
                // get palava instance data
                foreach($palava_config['actions'] as $aname => $action) {
                    if ($action['ENABLED'] != "true") {
                        continue;
                    }
                    if ($action['TYPE'] == "js") {
                        $result = array('name' => $aname, 'value' => runJS($palavaobj, $action['command']));
                    } elseif ($action['TYPE'] == "shell") {
                        $result = array('name' => $aname, 'value' => runShell($palavaobj, $action['command']));
                    }
                    // TODO implement checks for:  critical,warning,important,info
                    if (isset($action['ORDER'])) {
                        $result['status'] = 'ok';
                        if ($action['ORDER'] == 'asc') {
                            if (isset($action['CRITICAL']) && $action['CRITICAL'] < $result['value']) {
                                $result['status'] = 'critical';
                            } elseif (isset($action['WARNING']) && $action['WARNING'] < $result['value']) {
                                $result['status'] = 'warning';
                            } elseif (isset($action['IMPORTANT']) && $action['IMPORTANT'] < $result['value']) {
                                $result['status'] = 'important';
                            }
                        } else {
                            if (isset($action['CRITICAL']) && $action['CRITICAL'] > $result['value']) {
                                $result['status'] = 'critical';
                            } elseif (isset($action['WARNING']) && $action['WARNING'] > $result['value']) {
                                $result['status'] = 'warning';
                            } elseif (isset($action['IMPORTANT']) && $action['IMPORTANT'] > $result['value']) {
                                $result['status'] = 'important';
                            }
                        }
                    } else {
                        $result['status'] = 'none';
                    }
                    $result['attributes'] = $action;
                    $palava['results'][$aname] = $result;
                }
                
                // close the connection
                $palavaobj->close();
            }
                
            $palava['errors'] = $palavaobj->errors();
            $machine['palavas'][] = $palava;
            
        }
        
        // summarize possible rows in this machine
        $rows = array();
        foreach($machine['palavas'] as $palava) {
            foreach($palava['results'] as $result) {
                if (!in_array($result['name'], $rows)) {
                    $rows[] = $result['name'];
                }
            }
        }
        $machine['result_rows'] = $rows;
        
        $machines[] = $machine;
        
    }
}


// OUTPUT
header("Content-Type: application/xhtml+xml; charset=UTF-8");
echo '<?xml version="1.0" encoding="UTF-8" ?>';

?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
       "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <title><?php echo $config['NAME'] ?></title>
        <link rel="stylesheet" type="text/css" href="status.css" />
    </head>

    <body>
        <div>
            <h1><?php echo $config['NAME'] ?></h1>
            <p>Configuration: <?php echo $config['file'] ?></p>
            <p>Library: <?php echo $config['LIBRARY'] ?></p>
        </div>
        
        <?php if (count($error) > 0): ?>
        <div>
            <h2>Errors occured:</h2>
            <?php foreach($error as $err): ?>
            <pre><?php echo $err ?></pre>
            <?php endforeach ?>
        </div>
        <?php endif ?>
        <div>
            <?php foreach($machines as $machine): ?>
            <div class="machine">
                <h2><?php echo $machine['name'] ?> (<?php echo $machine['host'] ?>)</h2>
                <?php foreach($machine['results'] as $result): ?>
                    <p><b><?php echo $result['name'] ?></b>: <?php echo $result['value'] ?></p>
                <?php endforeach ?>
                <table>
                    <tr>
                        <th>NAME</th>
                        <th>PORT</th>
                        <th>VERSION</th>
                        <?php foreach($machine['result_rows'] as $result_row): ?>
                            <th><?php echo $result_row ?></th>
                        <?php endforeach ?>
                    </tr>
                    <?php foreach($machine['palavas'] as $palava): ?>
                    <tr>
                        <td class="left name"><?php echo $palava['name'] ?></td>
                        <td class="right"><?php echo $palava['port'] ?></td>
                        <?php if(!$palava['status']): ?>
                            <td class="center critical" colspan="100">DOWN</td>
                        <?php else: ?>
                            <td class="left"><?php echo $palava['version'] ?></td>
                            <?php foreach($machine['result_rows'] as $result_row): ?>
                                <?php echo formatResult($result_row, $palava['results']) ?>
                            <?php endforeach ?>
                        <?php endif ?>
                    </tr>
                        <?php if(count($palava['errors']) > 0): ?>
                        <td>&nbsp;</td>
                        <td colspan="100">
                            <pre><?php echo htmlspecialchars(implode("\n", $palava['errors'])) ?></pre>
                        </td>
                        <?php endif ?>
                    <?php endforeach ?>
                </table>
            </div>
            <?php endforeach ?>
        </div>
    </body>
</html>