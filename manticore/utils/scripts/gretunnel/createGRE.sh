#!/bin/bash
#
# This script creates a GRE tunnel interface in a linux host. 
# First of all, it loads the ip_gre module, which provides the management of GRE interfaces in a linux device.
# The user has to indicate the local and remote addresses that will be connected by the tunnel, and also
# the ip address that will be assigned to the logical interface.
# Optionally, a static route to a remote private network can be defined through the next-hop argument.
#
# Ex: createGRE.sh gre0 local 147.34.2.1 remote 193.123.12.3 address 10.10.10.1
# Ex: createGRE.sh gre0 local 147.34.2.1 remote 193.123.12.3 address 10.10.10.1 route 192.168.1.0/24
#

if [ $# -eq "7" ]
then
  route=true;
elif [ $# -eq "9" ]
then
  route=true;
else
  echo "Usage: createGRE.sh interface [local] source_address [remote] target_address [address] tunnel_address"
  echo "Usage: createGRE.sh interface [local] source_address [remote] target_address [address] tunnel_address [route] next-hop"
  exit	
fi

modul='ip_gre'
interface=$1;

if [ $2 == "local" ]
then
  s_address=$3
elif [ $2 == "remote" ]
then
  d_address=$3
elif [ $2 == "address" ]
then
  address=$3
elif [ $2 == "route" ]
then 
  nexthop=$3
fi

if [ $4 == "local" ]
then
  s_address=$5
elif [ $4 == "remote" ]
then
  d_address=$5
elif [ $4 == "address" ]
then
  address=$5
elif [ $4 == "route" ]
then 
  nexthop=$5
fi

if [ $6 == "local" ]
then
  s_address=$7
elif [ $6 == "remote" ]
then
  d_address=$7
elif [ $6 == "address" ]
then
  address=$7
elif [ $6 == "route" ]
then 
  nexthop=$7
fi

if [ $# -eq "9" ]
then
  if [ $8 == "local" ]
  then
    s_address=$9 
  elif [ $8 == "remote" ]
  then
    d_address=$9
  elif [ $8 == "address" ]
  then
    address=$9
  elif [ $8 == "route" ]
  then 
    nexthop=$9
  fi
fi


echo "Loading $modul modul..."
modprobe $modul

echo "Configuring $interface interface..."
iptunnel add $interface mode gre local $s_address remote $d_address

echo "Setting up $interface interface..."
ip link set $interface up

echo "Setting $interface address..."
ip addr add $address dev $interface

if [ $# -eq "9" ]
then
  echo "Setting route to remote network..."
  ip route add $nexthop dev $interface
fi

echo "Status: OK"
echo "Tunnel interface: $interface"
echo "Tunnel Address: $address"
echo "Source Address: $s_address"
echo "Destination Address: $d_address"
if [ $# -eq "9" ]
then
  echo "Routing $nexthop through $interface interface"
fi
