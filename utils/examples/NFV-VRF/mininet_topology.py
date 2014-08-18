#!/usr/bin/python                                                                                                             
# 2014 @ josep.batalle@i2cat.net                                                                                                     

"""                                                                                                                           
This file create a topology with 4 switchs connected to 2 controllers.                                                        
"""                                                                                                                           
import os                                                                                                                     
import sys                                                                                                                    
import json                                                                                                                   
import time                                                                                                                   
from mininet.net import Mininet                                                                                               
from mininet.node import Controller, OVSKernelSwitch, RemoteController                                                        
from mininet.cli import CLI                                                                                                   
from mininet.log import setLogLevel, info                                                                                     
from mininet.net import Link                                                                                                  

ip_Ctrl1 = "127.0.0.1"
ip_Ctrl2 = "127.0.0.1"
port_Ctrl1 = 6633
port_Ctrl2 = 6633

def multiControllerNet():                                                                                                     
	info( '*** Creating demo network with eight Switches and three Controllers\n' )

        # Create an empty network.
        net = Mininet(controller=RemoteController, switch=OVSKernelSwitch)
        print "*** Creating controllers"
        c1 = net.addController( 'c1', ip=ip_Ctrl1, port=port_Ctrl1 )
        c2 = net.addController( 'c2', ip=ip_Ctrl2, port=port_Ctrl2 )

        print "*** Creating switches"
        s1 = net.addSwitch( 's1', mac="00:00:00:00:00:01")
        s2 = net.addSwitch( 's2', mac="00:00:00:00:00:02")
        s3 = net.addSwitch( 's3', mac="00:00:00:00:00:03")
        s4 = net.addSwitch( 's4', mac="00:00:00:00:00:04")
        
        print "*** Creating hosts"
        h1 = net.addHost( "h1" , ip="10.0.1.1" )
        h2 = net.addHost( "h2" , ip="10.0.2.2" )
        
        print "*** Creating links"
        s1.linkTo( s2 )
        s1.linkTo( s3 )
        s2.linkTo( s4 )
        s3.linkTo( s4 )

	s1.linkTo( h1 )
        s4.linkTo( h2 )
        
        net.build()
        s1.start( [ c1 ] )
        s2.start( [ c1 ] )
        s3.start( [ c2 ] )
        s4.start( [ c2 ] )


	os.popen( "sudo ovs-vsctl set bridge s1 stp_enable=true")
        os.popen( "sudo ovs-vsctl set bridge s2 stp_enable=true")
        os.popen( "sudo ovs-vsctl set bridge s3 stp_enable=true")
        os.popen( "sudo ovs-vsctl set bridge s4 stp_enable=true")
        
        h1.cmd("/usr/sbin/sshd");
        h2.cmd("/usr/sbin/sshd");
        
        print "*** Running CLI"
        CLI( net )
        print "*** Stopping network"
        net.stop()

if __name__ == '__main__':
        setLogLevel( 'info' )  # for CLI output
        multiControllerNet()


