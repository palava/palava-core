<?php
/*
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * @license GNU General Public License (GPL)
 * @author Tobias Sarnowski <sarnowski@cosmocode.de>
 */

define('COOKIE_SESSION_NAME', 'ccsess');

define('SEND_BUFFER_SIZE', 512);

/**
 * Provides functionality to communicate with the Palava backend
 */
class Palava
{
    private $debug = false;

    private $config = array();

    // connection infos
    private $besocket = null;
    private $besocket_timeout = null;
    private $besocket_errors = array();
    private $beserver = null;
    private $beserver_active = null;

    private $session_started = false;
    private $session_id = "";

    private $known_mimetypes = array('error' => 'application/error',
                                     'php' => 'application/x-httpd-php',
                                     'json' => 'application/json',
                                     'text' => 'text/plain',
                                     'xml'  => 'application/xml');
    private $actual_job = null;
    private $benchmarks = array();

    /**
     * Initialize the Palava client Library
     *
     * Initiates the backend connection using the given config file
     *
     * You can choose between linear or random connecting, when mutliple
     * backend servers are available. FIXME more info!
     *
     * @param string $configfile     - a Palava config file
     * @param bool   $random_connect - true for random, false for linear conects
     * @param int    $timeout        - connection timeout in seconds
     */
    public function Palava($configfile, $random_connect = true, $timeout = 3)
    {
        // parse config file
//        if (strpos(':', $configfile) !== false)
//        {
//            $this->config['BACKEND_SERVERS'] = $configfile;
//        }
//        elseif (!file_exists($configfile))
//        {
//            $this->besocket_errors[] = "No config file found! ($configfile)";
//            return;
//        }
//        else
//        {
//            $configfile = file($configfile);
//            foreach ($configfile as $line)
//            {
//                $line = trim($line);
//                if (!(substr($line, 0, 1) == '#') && !(strlen($line) == 0))
//                {
//                    $firsteq = strpos($line, '=');
//                    if ($firsteq >= 0)
//                    {
//                        $key = trim(substr($line, 0, $firsteq));
//                        $value = trim(substr($line, $firsteq + 1));
//                        if (substr($value, 0, 1) == '"' && substr($value, strlen($value) - 1, 1) == '"')
//                        {
//                            $value = substr($value, 1);
//                            $value = substr($value, 0, strlen($value) - 1);
//                        }
//                        $this->config[$key] = $value;
//                    }
//                }
//            }
//    }
//
//        // parse backend servers
//        if (!isset($this->config['BACKEND_SERVERS']))
//        {
//            $this->besocket_errors[] = 'Missing BACKEND_SERVERS in config file!';
//            return;
//        }
        $beserver = explode(';', $configfile);
        
        // inititate connection
        $this->beserver = $beserver;
        $this->beserver_timeout = $timeout;

        if ($random_connect)
        {
            $this->connect_random();
        }
        else
        {
            $this->connect_linear();
        }
        @socket_clear_error($this->besocket);
        
        $this->send_request('open', '', array(json_encode($_SERVER)));
        $this->receive_response();
    }

    /**
     *  Check if the connection is established
     *
     * @return bool - true when connection established
     */
    public function established()
    {
        return ($this->besocket != null);
    }

    /**
     * Returns the active backend server
     *
     * if not connected, returns false
     *
     * @return mixed
     */
    public function server()
    {
        if ($this->established())
        {
            return $this->beserver[$this->beserver_active];
        }
        else
        {
            return false;
        }
    }

    /**
     * Return the backend version
     *
     * @return int - the version
     */
    public function getBackendVersion()
    {
        return $this->call("@palava.system.version");
    }

    /**
     * closes an established connection
     */
    public function close()
    {
        if ($this->established())
        {
            
//            $this->call('@palava.system.close');
            $this->send_request('close', '', array('{}'));
            socket_close($this->besocket);
            $this->besocket = null;
        }
    }

    /**
     * Initialize a session
     *
     * Opens a new session or reopens an existing one. Needs to be called
     * before any headers were sent.
     */
    public function session_start()
    {
        $this->session_started = true;
        $sessionid = $this->call('@palava.session.initialize',
                                 'ip='.$_SERVER['REMOTE_ADDR'],
                                 'ua='.$_SERVER['HTTP_USER_AGENT'],
                                 'url='.$_SERVER['REQUEST_URI']);
        $this->setSessionID($sessionid);
    }

    /**
     *  Destroys the current session
     */
    public function session_destroy()
    {
        $this->call('@palava.session.destroy');
        $this->setSessionID('');
        $this->session_started = false;
    }

    /**
     * Returns the currently assigned SessionID
     *
     * @returns int - SessionID
     */
    public function getSessionID()
    {
        return $this->session_id;
    }

    /**
     * Sets a new SessionID and sends a session cookie
     *
     * Needs to be called before headers where sent
     */
    public function setSessionID($session_id)
    {
        $this->session_id = $session_id;
        setcookie(COOKIE_SESSION_NAME, $session_id, 0, '/');
    }

    /**
     * Call a backend job of type 'data'
     *
     * You can give multiple $data pairs
     *
     * @param string $job - name of the backend Job
     * @param string $data - name=value pair
     * @returns mixed - false on error, parsed data on success
     */
    public function call()
    {
        $args = func_get_args();
        return $this->call_ex('data', $args);
    }

    /**
     * Call a backend job of type 'text'
     *
     * You can give multiple $data pairs
     *
     * @param string $job - name of the backend Job
     * @param string $data - name=value pair
     * @returns mixed - false on error, parsed data on success
     */
    public function call_text()
    {
        $args = func_get_args();
        return $this->call_ex('text', $args);
    }

    public function call_json($job,$obj)
    {
        if(count($obj) > 0)
            $json = json_encode($obj);
        else
            $json = '{}';

        return $this->call_ex('json', array($job, $json));
    }

    /**
     * Call a backend job of the given type
     *
     * You can give multiple $data pairs
     *
     * @param string $type - type of the backend Job
     * @param array $arguments - jobname and key=value pairs of data
     * @returns mixed - false on error, parsed data on success
     */
    public function call_ex($type, $args)
    {
        if (count($args) < 1)
        {
            die("not enough arguments for call*");
        }

        $job = $args[0];
        $this->actual_job = $job;
        $this->startBench($job);

        $request = array();
        for ($n = 1; $n < count($args); $n++)
        {
            $request[] = $args[$n];
        }
        $this->send_request($type, $job, $request);
        //
        // let the backend do his things..
        //
        $response = $this->receive_response();

        $this->setBenchPoint($job, "end");

        return $response;
    }

    /**
     * Sends a local file to a backend job
     *
     * @param string $job  - name of the backend job
     * @param string $file - path to a local file
     * @returns mixed - false on error, job data on success
     */
    public function uploadfile($job, $file)
    {
        if (!file_exists($file))
        {
            die("Ciritcal error, uploadfile not found!");
        }
        return $this->call_ex('binary', array($job, $file));
    }

    /**
     * Enables/Disables debugging output
     *
     * @param boolean $debug - set to true fro debug output
     */
    public function setDebug($debug)
    {
        $this->debug = $debug;
    }

    /**
     * Returns all collected errors
     *
     * @return array - list of errors
     */
    public function errors()
    {
        return $this->besocket_errors;
    }

    /**
     * Sends the given raw data to the backend
     *
     * @param string $data - raw protocol data
     */
    public function data_send($data)
    {
        if (!$this->established())
        {
            $this->besocket_errors[] = 'data_send() failed:  no connection established';
            return false;
        }

        if ($this->debug)
            echo "\n=>  $data\n";

        $sent_bytes = 0;
        while ($sent_bytes < strlen($data))
        {
            $sent_bytes = @socket_write($this->besocket, $data);
            if (!$sent_bytes)
            {
                die("Connection to backend lost (write)!");
            }
        }
    }

    /**
     * Reads incoming data from the backend
     *
     * Binary data will be streamed to the browser immediately, all other
     * data is returned
     *
     * @returns mixed - the read data
     */
    public function data_receive()
    {
        if (!$this->established())
        {
            $this->besocket_errors[] = 'data_receive() failed:  no connection established';
            return false;
        }

        // read header
        $part = 0;
        $_mimetype = "";
        $_contentlength = "";

        while (true)
        {
            $data = @socket_read($this->besocket, 1);
            if (socket_last_error($this->besocket))
            {
                die("Connection to backend lost (read)!");
                break;
            }

            switch ($part)
            {
                case 0:  // mime-type ://
                    if ($data == ':')
                    {
                        $part++;
                    }
                    else
                    {
                        $_mimetype .= $data;
                    }
                    break;
                case 1:
                case 2:
                    if ($data == '/')
                    {
                        $part++;
                    }
                    else
                    {
                        die("palava protocol error 1");
                    }
                    break;
                case 3:  //  ( content-lengt )
                    if ($data == '(')
                    {
                        $part++;
                    }
                    else
                    {
                        die("palava protocol error 2");
                    }
                    break;
                case 4:
                    if ($data == ')')
                    {
                        $part++;
                    }
                    else
                    {
                        $_contentlength .= $data;
                    }
                    break;
                case 5:
                    if ($data == '?')
                    {
                        break 2;
                    }
                    else
                    {
                        die("palava protocol error 3");
                    }
            }
        }

        if (!$data)
        {
            echo socket_strerror(socket_last_error($this->besocket));
            socket_clear_error($this->besocket);
        }

        $response['type'] = $_mimetype;
        $response['length'] = intval($_contentlength);

        // now ship the content
        if (in_array($response['type'], $this->known_mimetypes))
        {
            $response['body'] = "";

            while (strlen($response['body']) < $response['length'])
            {
                $br = $response['length'] - strlen($response['body']);
                $response['body'] .= socket_read($this->besocket, $br);
                if (socket_last_error($this->besocket))
                {
                    die("Connection to backend lost (read)!");
                    break;
                }
            }

            if ($this->debug)
            {
                print_r($response);
            }

            if ($response['type'] == $this->known_mimetypes['error'])
            {
                echo $response['body'];
            }

            return $response;
        }
        else
        {
            // send it plain
            header("Content-Type: ".$response['type']);
            header("Content-Length: ".$response['length']);

            $buffer_size = SEND_BUFFER_SIZE;

            $sent = 0;
            while ($sent < $response['length'])
            {
                if ($buffer_size > $response['length'] - $sent)
                {
                    $buffer_size = $response['length'] - $sent;
                }
                $buffer = socket_read($this->besocket, $buffer_size);
                echo $buffer;
                $sent += strlen($buffer);
            }

            $this->close();
            die();
        }

    }

    /**
     * Processe incoming data
     *
     * Reads incoming data from the backend and either streams it to the browser
     * or returns it in the correct (parsed) format
     */
    public function receive_response()
    {
        $data = $this->data_receive();

        if ($data['type'] == $this->known_mimetypes['php'])
        {
            $cmd = '$result = '.$data['body'].';';
            eval($cmd);
            return $result;
        }
        if ($data['type'] == $this->known_mimetypes['text'])
        {
            return $data['body'];
        }
        if ($data['type'] == $this->known_mimetypes['json'])
        {
            //return $data['body'];
            return json_decode($data['body'], true);
        }
        if ($data['type'] == $this->known_mimetypes['xml'])
        {
            return $data['body'];
        }
        return null;
    }

    /**
     * Initialize benchmarking
     *
     * Will time the execution of the following script execution
     *
     * @param string $name - FIXME
     */
    public function startBench($name)
    {
        if (!isset($this->benchmarks[$name]))
            $this->benchmarks[$name] = array();

        $this->benchmarks[$name]['start'] = microtime();
    }

    /**
     * FIXME
     */
    public function setBenchPoint($name, $description)
    {
        $this->benchmarks[$name][$description] = microtime();
    }

    /**
     * Outputs the benchmark times, HTML formatted
     */
    public function printBenchmarks()
    {
        foreach ($this->benchmarks as $name => $benchmark)
        {
            echo "<b>$name</b><br />";

            $time = $benchmark['start'];
            foreach ($benchmark as $desc => $t)
            {
                $diff = $t - $time;
                $time = $t;

                $diff *= 1000;
                echo "  $desc:\t<b>$diff</b>ms<br />";
            }
        }
    }

    /**
     * Connect to all servers randomly
     *
     * @return bool - true on success
     */
    private function connect_random()
    {
        while (count($this->beserver) > 0)
        {
            $active = array_rand($this->beserver);

            if ($this->connect($this->beserver[$active]))
            {
                $this->beserver_active = $active;
                return true;
            }
            else
            {
                unset($this->beserver[$active]);
                shuffle($this->beserver);
            }
        }
        return false;
    }

    /**
     * Connect to all servers in linear order
     *
     * @return bool - true on success
     */
    private function connect_linear()
    {
        for ($n = 0; $n < count($this->beserver); $n++)
        {
            if ($this->connect($this->beserver[$n]))
            {
                $this->beserver_active = $n;
                return true;
            }
        }
        return false;
    }

    /**
     * Connect to a given server address
     *
     * @param string $address - server in format 'host:port'
     * @return bool - true on success
     */
    private function connect($address)
    {
        // our address to connect
        list($host, $port) = split(':', $address);

        // create a socket
        if (!($newsocket = @socket_create(AF_INET, SOCK_STREAM, getprotobyname("tcp"))))
        {
            $this->besocket_errors[] = 'socket_create() failed:  '.socket_strerror(socket_last_error());
            return false;
        }

        // set timeout
        if (!@socket_set_option($newsocket, SOL_SOCKET, SO_RCVTIMEO, array('sec' => $this->besocket_timeout, 'usec' => 0)))
        {
            $this->besocket_errors[] = 'socket_set_option() failed:  '.socket_strerror(socket_last_error());
            return false;
        }

        // connect!
        if (!@socket_connect($newsocket, $host, $port))
        {
            $this->besocket_errors[] = 'socket_connect() failed:  '.socket_strerror(socket_last_error());
            return false;
        }

        // we are connected
        $this->besocket = $newsocket;


        return true;
    }

    /**
     *  Formats the given request data and sends it to the backend
     *
     * @param string $type    - data, text or binary
     * @param string $jobname - the backend job to call
     * @param string $request - the data to send
     */
    private function send_request($type, $jobname, $request)
    {
        // more information at http://palava.cosmocode.de/wiki/protocol

        // body_len
        if ($type == 'data') {
            $params = array();
            foreach($request as $i => $paramarr) {
                if (is_array($paramarr)) {
                    foreach ($paramarr as $key => $param) {
                        $params[$key] = $param;
                    }
                } else {
                    $splitted = explode('=', $paramarr);
                    $params[$splitted[0]] = $splitted[1];
                }
            }
            $body = json_encode($params);
            $body_len = strlen($body);
        }
        elseif ($type == 'text')
        {
            $body = $request[0];
            $body_len = strlen($body);
        }
        elseif ($type == 'json' || $type == 'open' || $type == 'close')
        {
            $body = $request[0];
            $body_len = strlen($body);
        }
        elseif ($type == 'binary')
        {
            $file = $request[0];
            $body_len = filesize($file);
        }

        // session_id
        $session_id = "";
        if (TRUE OR $this->session_started)
        {
            if ($this->getSessionID() == "")
            {
                if (isset($_COOKIE[COOKIE_SESSION_NAME]))
                    $session_id = $_COOKIE[COOKIE_SESSION_NAME];
                elseif (isset($_POST[COOKIE_SESSION_NAME]))
                    $session_id = $_POST[COOKIE_SESSION_NAME];
                elseif (isset($_GET[COOKIE_SESSION_NAME]))
                    $session_id = $_GET[COOKIE_SESSION_NAME];
            }
            else
            {
                $session_id = $this->getSessionID();
            }
        }

        // form the header
        $header = "$type://$jobname/$session_id/($body_len)?";

        // send the packet
        if ($type != 'binary')
        {
            $request_data = $header.$body;
            $this->data_send($request_data);
        }
        else
        {
            $this->data_send($header);
            $fpd = fopen($file, 'r');
            while (!feof($fpd))
            {
                $data = fgets($fpd, SEND_BUFFER_SIZE);
                $this->data_send($data);
            }
            fclose($fpd);
        }
    }

    function escapeString($string)
    {
    $string = str_replace('\\', '\\\\', $string);
    $string = str_replace('&', '\\&', $string);
    $string = str_replace('=', '\\=', $string);
    return $string;
    }
}

?>
