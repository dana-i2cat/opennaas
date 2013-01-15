package org.opennaas.extensions.sampleresource.capability.example;
import java.io.*;
import java.util.*;

public class PLink implements Serializable
    {
        public int id;
        public int node1Id;
        public int node2Id;
        public int capacity;
        //public int availableCapacity;        
        public int delay;

        public PLink()
        {
            id = -1;
        }
         
    }
