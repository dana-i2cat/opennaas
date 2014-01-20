#!/usr/bin/python

"""
Script created by VND - Visual Network Description
"""
from mininet.net import Mininet
from mininet.node import Controller, RemoteController, OVSKernelSwitch
from mininet.cli import CLI
from mininet.log import setLogLevel
from mininet.link import Link, TCLink

def topology():
    "Create a network."
    net = Mininet( controller=RemoteController, link=TCLink, switch=OVSKernelSwitch )

    print "*** Creating nodes"
    s1 = net.addSwitch( 's1' )
    s2 = net.addSwitch( 's2' )
    s3 = net.addSwitch( 's3' )
    s4 = net.addSwitch( 's4' )
    s5 = net.addSwitch( 's5' )
    h6 = net.addHost( 'h6', mac='00:00:00:00:00:06', ip='192.168.10.10/24' )
    h7 = net.addHost( 'h7', mac='00:00:00:00:00:07', ip='192.168.10.20/24' )
    h8 = net.addHost( 'h8', mac='00:00:00:00:00:08', ip='192.168.10.11/24' )
    h9 = net.addHost( 'h9', mac='00:00:00:00:00:09', ip='192.168.10.21/24' )
    c10 = net.addController( 'c10', controller=RemoteController, ip='127.0.0.1', port=6633)

    print "*** Creating links"
    net.addLink(s3, s1, 1, 3)
    net.addLink(s4, s3, 3, 4)
    net.addLink(s2, h8, 12, 2)
    net.addLink(h6, s5, 2, 12)
    net.addLink(s1, h9, 12, 1)
    net.addLink(h7, s4, 1, 12)
    net.addLink(s1, s2, 2, 1)
    net.addLink(s4, s1, 1, 4)
    net.addLink(s5, s4, 4, 5)
    net.addLink(s5, s3, 3, 5)
    net.addLink(s3, s2, 2, 3)

    print "*** Starting network"
    net.build()
    s2.start( [c10] )
    s1.start( [c10] )
    s3.start( [c10] )
    s5.start( [c10] )
    s4.start( [c10] )
    c10.start()

    print "*** Print Ip addresses"
    h6.cmd("ifconfig h6-eth2 192.168.10.10 netmask 255.255.255.0 up")
    h7.cmd("ifconfig h7-eth1 192.168.10.20 netmask 255.255.255.0 up")
    h8.cmd("ifconfig h8-eth2 192.168.10.11 netmask 255.255.255.0 up")
    h9.cmd("ifconfig h9-eth1 192.168.10.21 netmask 255.255.255.0 up")
	
    print "*** Running CLI"
    CLI( net )

    print "*** Stopping network"
    net.stop()

if __name__ == '__main__':
    setLogLevel( 'info' )
    topology()

