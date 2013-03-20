/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.extensions.vnmapper.capability.example;

import java.io.*;
import java.util.*;
import java.util.HashSet;
/**
 *
 * @author ahammaa
 */

/// the next class is to store the result of mapping/reserved resources on nodes and links
class MappingResult
    {
        public ArrayList nodes = new ArrayList();
        public ArrayList vnodes = new ArrayList();       
        public ArrayList< ArrayList > links = new ArrayList< ArrayList >();
        public ArrayList<ArrayList<VNTLinkMappingCell> > VNTLinkMappingArray=new ArrayList<ArrayList<VNTLinkMappingCell>>();
        public int providerID;
        public int cost;
        public void print(){
            System.out.println("Mapping Result: ");
            System.out.println("vnodes: ");
            for(int i=0;i<vnodes.size();i++)
            {
                System.out.println(i+"--"+Integer.valueOf(vnodes.get(i).toString()));
            }
            System.out.println("vlinks: ");
            for(int i=0;i<(int)VNTLinkMappingArray.size();i++){
                       for(int j=0;j<(int)VNTLinkMappingArray.get(i).size();j++)
                              	  {
                              		 if(VNTLinkMappingArray.get(i).get(j).isMapped==1)
                              		            {
                              			          if(i<j){
                                                             System.out.print(i+"--"+j+":"); 
                                                             VNTLinkMappingArray.get(i).get(j).resultPath.node1Id=Integer.valueOf(vnodes.get(i).toString());
                                                             VNTLinkMappingArray.get(i).get(j).resultPath.node2Id=Integer.valueOf(vnodes.get(j).toString());
                                                             VNTLinkMappingArray.get(i).get(j).resultPath.printPath();
                                                             System.out.println();
                                                          }
                                                          else
                                                          {
                                                              System.out.print(j+"--"+i+":"); 
                                                              VNTLinkMappingArray.get(j).get(i).resultPath.node1Id=Integer.valueOf(vnodes.get(j).toString());
                                                             VNTLinkMappingArray.get(j).get(i).resultPath.node2Id=Integer.valueOf(vnodes.get(i).toString());
                                                             VNTLinkMappingArray.get(j).get(i).resultPath.printPath();
                                                             System.out.println();
                                                          }
                                                    }
                                  }
            }
        }
}

/// the next two classes are used to store information during the mapping process
class VNTLinkMappingCell
    {
        public int isConnected;
        public int isMapped;
        public Path resultPath=new Path();
        public VNTLinkMappingCell()
        {
            isConnected = 0;
            isMapped = 0;
        }
    }

class VNTNodeMappingCell
    {
        public int vid;
        public ArrayList possibleRealNodes=new ArrayList();
        public int chosenRealNode;
        public int connectionsNum; // number of connection to the already mapped vnodes
        public int pointer;
    }

///// the next class is resposible for matching and mapping///
public class VNTMapper
    {
        
       //// the next method sorts the virtual nodes based on nodes degrees
        public ArrayList vNodesSorting(VNTRequest v)
        {
            //cout<<"------------------------------sorting based on virtual nodes degrees---------------------------------"<<endl;
	        ArrayList degrees=new ArrayList();
	        ArrayList res=new ArrayList();

	        for(int i=0;i<v.vnodeNum;i++) degrees.add(0);;
	        for(int i=0;i<v.vnodeNum;i++){
               for(int j=0;j<v.vnodeNum;j++){
    	           if((v.connections.get(i).get(j).id==1)) 
                   {
                       int d1=Integer.valueOf(degrees.get(i).toString());
                       int d2=Integer.valueOf(degrees.get(j).toString());
                       d1++;d2++;
                       degrees.remove(i);degrees.add(i,d1);
                       degrees.remove(j);degrees.add(j,d2);
                   }
              }
	        }
           
            res.add(0);
            int u;
            for(int i=1;i<v.vnodeNum;i++){
   	         u=0;

   	        while (((Integer.valueOf((degrees.get(Integer.valueOf(res.get(u).toString()))).toString()))>=(Integer.valueOf(degrees.get(i).toString()))&&(u<i)))
   	           {
   	    	         u++;
   	           }
   	        for(int e=i;e>u;e--){
                           int temp=Integer.valueOf(res.get(e-1).toString());
                           res.remove(e);
                           res.add(e,temp);
   	        	   
   	           }
                   res.remove(u);
                   res.add(u,i);
   	          
   	         }

            return res;
          }
        
        //// the next method sorts the virtual nodes based on the number of candidate physical nodes
        public ArrayList vNodesCandidatePNodesSorting(ArrayList<VNTNodeMappingCell> VNTNodeMappingArray)
        {
            //cout<<"------------------------------sorting based on candidate physical nodes number---------------------------------"<<endl;
	      ArrayList res=new ArrayList();
	      int j=0;	      
            for (int y = 0; y < VNTNodeMappingArray.size(); y++)
             {
                res.add(0);
             }

	       for(int i=1;i<(int)VNTNodeMappingArray.size();i++){
	        j=0;
	       while (((VNTNodeMappingArray.get(Integer.valueOf(res.get(j).toString())).possibleRealNodes.size())<=(VNTNodeMappingArray.get(i).possibleRealNodes.size()))&&(j<i))
	           {
	    	         j++;
	           }

	           for(int k=i;k>j;k--){
                           int temp=Integer.valueOf(res.get(k-1).toString());
                           res.remove(k);
                           res.add(k,temp);  	   
	           }
                   res.remove(j);
                   res.add(j,i);
	           
	       }

            return res;
        }

        // the next method maps the virtual link to a physical path//      
        public Path mapVirtualLink(VNTRequest v,InPNetwork net, int v1id,int v2id,int v1RealNode,int v2RealNode)
        {
            
	        int requiredB;
	        int requiredD;	       
	        Path p=new Path();
            if(v1id<v2id){              
              requiredB=v.connections.get(v1id).get(v2id).capacity;
              requiredD=v.connections.get(v1id).get(v2id).delay;
    
            }else{
    	   
    	    requiredB=v.connections.get(v2id).get(v1id).capacity;
    	    requiredD=v.connections.get(v2id).get(v1id).delay;
    	
         }
        

           if(Global.pathChoice==1)  //// shortest path
               p=net.findPathBetweemTwoNodes(v1RealNode,v2RealNode,requiredB,requiredD,Global.maxPathLinksNum);
           else      /// load balancing
	       p=net.findPathBetweemTwoNodes2(v1RealNode,v2RealNode,requiredB,requiredD,Global.maxPathLinksNum);
            return p;
        }

       
   //// the next method is the main method to map a VNT         
   public int VNTMapping(VNTRequest v,InPNetwork net,int steps,ArrayList<ArrayList > nodesMatching, int option,MappingResult mappingResult)
    {
        int res=1;
      	 ArrayList<ArrayList > nodesMapping=new ArrayList<ArrayList>();         

         nodesMapping=nodesMatching;
         
	  for(int i=0;i<(int)nodesMapping.size();i++){
	   if((int)nodesMapping.get(i).size()==0){
		   res=0;
	   }
	  }
          
	 if(res!=0){
	  /// initilaizing the array to store the current status of each virtual node including:
		/// possible real node set
		/// the current chosen real node
		/// pointer to the previous virtual node in the way to map the virtual node
	     ArrayList<VNTNodeMappingCell> VNTNodeMappingArray=new ArrayList<VNTNodeMappingCell>();
	     ArrayList sortedVNodesSet=new ArrayList();
	     for(int i=0;i<(int)v.vnodes.size();i++){
		     VNTNodeMappingCell vNodeCell=new VNTNodeMappingCell();
		     vNodeCell.vid=i;
		     vNodeCell.possibleRealNodes=nodesMapping.get(vNodeCell.vid);

		     vNodeCell.chosenRealNode=-1;
		     vNodeCell.pointer=-1;
		     vNodeCell.connectionsNum=0;
                     VNTNodeMappingArray.add(vNodeCell);		    
	     }
	      
             //// sort the virtual node starting from the virtual node that has the least number of physical nodes
             sortedVNodesSet = vNodesCandidatePNodesSorting(VNTNodeMappingArray);
     
	     /// initializing the array to store the current status of each virtual link (if its been mapped or not yet)
	     ArrayList<ArrayList<VNTLinkMappingCell> > VNTLinkMappingArray=new ArrayList<ArrayList<VNTLinkMappingCell>>();
	     
	     for(int i=0;i<(int)v.vnodeNum;i++){
             VNTLinkMappingArray.add(new ArrayList<VNTLinkMappingCell>());		
             for (int j = 0; j < v.vnodeNum;j++ )
             {
                 VNTLinkMappingArray.get(i).add(new VNTLinkMappingCell());
             }
	  }

	    //// initializing the set to store the current selected physical nodes so that a physical node can be selected only once
	      IntSet selectedRealNodes=new IntSet();
	     
              /// initializing a set to store the successfully mapped virtual nodes
	      IntSet mappedVNTNodes=new IntSet();
              
            //// call the method that is resposible for the recursive backtracking job of composing the VNT
            Global.stepsNum=0;
            res = VNTMappingFunc(v, net, selectedRealNodes, VNTNodeMappingArray, VNTLinkMappingArray, sortedVNodesSet, mappedVNTNodes);
            
            //// calculating the mapping result and cost of it //
            if(res==1)
            {
              
                /// initialize mappingResult///
              for (int u1 = 0; u1 < net.nodeNum; u1++)
              {
                  mappingResult.nodes.add(0);                 
                  mappingResult.links.add(new ArrayList());
                  for (int u2 = 0; u2 < net.nodeNum; u2++)
                  {
                      mappingResult.links.get(u1).add(0);
                  }
              }
              for (int u1 = 0; u1 < v.vnodeNum; u1++)
              {
                    mappingResult.vnodes.add(0);                 
              }
             int nodeCosts=0; 
             int linkCosts=0; 
             for(int i=0;i<v.vnodes.size();i++)
             {
            	 nodeCosts=nodeCosts+v.vnodes.get(i).capacity;            	 
                 int chosen=VNTNodeMappingArray.get(i).chosenRealNode;                 
                 if(chosen!=-1)
                 {               
                    mappingResult.nodes.remove(chosen);
                    mappingResult.nodes.add(chosen, v.vnodes.get(i).capacity);
                    mappingResult.vnodes.remove(i);
                    mappingResult.vnodes.add( i,chosen);
                 }
              }
             mappingResult.VNTLinkMappingArray=VNTLinkMappingArray;
             for(int i=0;i<(int)VNTLinkMappingArray.size();i++){
                       for(int j=0;j<(int)VNTLinkMappingArray.get(i).size();j++)
                              	  {
                              		 if(VNTLinkMappingArray.get(i).get(j).isMapped==1)
                              		            {
                              			          if(i<j){
                              			                linkCosts=linkCosts+v.connections.get(i).get(j).capacity*VNTLinkMappingArray.get(i).get(j).resultPath.links.size();
                              			                        
                                                                for (int u = 0; u < VNTLinkMappingArray.get(i).get(j).resultPath.links.size();u++ )
                                                                {
                                                                    int temp=Integer.valueOf(mappingResult.links.get(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node1Id).get(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node2Id).toString());
                                                                    temp+=v.connections.get(i).get(j).capacity;
                                                                    mappingResult.links.get(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node1Id).remove(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node2Id);
                                                                    mappingResult.links.get(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node1Id).add(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node2Id,temp);
                                                                }
                                                                   ///
                              			              }else
                              			                 {
                              			              	 linkCosts=linkCosts+v.connections.get(j).get(i).capacity*VNTLinkMappingArray.get(j).get(i).resultPath.links.size();                              			                    	 
                                                                 ///
                                                                 for (int u = 0; u < VNTLinkMappingArray.get(i).get(j).resultPath.links.size(); u++)
                                                                 {
                                                                     int temp=Integer.valueOf(mappingResult.links.get(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node1Id).get(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node2Id).toString());
                                                                    temp+=v.connections.get(j).get(i).capacity;
                                                                    mappingResult.links.get(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node1Id).remove(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node2Id);
                                                                    mappingResult.links.get(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node1Id).add(VNTLinkMappingArray.get(i).get(j).resultPath.links.get(u).node2Id,temp);
                                                                  

                                                                     //mappingResult.links[VNTLinkMappingArray.get(i).get(j).resultPath.links[u].node1Id][VNTLinkMappingArray.get(i).get(j).resultPath.links[u].node2Id] += v.connections.get(j).get(i).capacity;
                                                                 }
                                                                   ///
                              			             }
                              		              }
                              	      }
                                }
          

               mappingResult.cost = nodeCosts + linkCosts;           
             
          }
         
          //VNTLinkMappingArray.clear();
          VNTNodeMappingArray.clear();
          sortedVNodesSet.clear();
          selectedRealNodes.clear();

      }
     return res;
    }

  //// the next method is resposible for the backtracking job of composing the VNT by mapping the vnodes and vlinks and building the subgraph until it is equal to the VNT graph
   public int VNTMappingFunc(VNTRequest v, InPNetwork net, IntSet selectedRealNodes, ArrayList<VNTNodeMappingCell> VNTNodeMappingArray, ArrayList<ArrayList<VNTLinkMappingCell>> VNTLinkMappingArray, ArrayList sortedVNodesSet, IntSet mappedVNTNodes)
   {
       
       int mappingFinish=0;
  
       //// select the current virtual node       
       int currentVNodeId=-1;  

       //currentVNodeId=chooseNextVNode(VNTNodeMappingArray,sortedVNodesSet);
         currentVNodeId=chooseNextVNodeMod(VNTNodeMappingArray);

      if(currentVNodeId!=-1){

	 int currentRealNode;
  	 InPNetwork originalNet=new InPNetwork();         
         try{
             originalNet = (InPNetwork)ObjectCopier.deepCopy(net);
         }catch(Exception e){System.out.println("Exception33 ///");}
         
	 Path resultedPath=new Path();
         
         //// sort the possible candidate physical nodes
         if(Global.PNodeChoice==1)  /// cost reduction
         VNTNodeMappingArray.get(currentVNodeId).possibleRealNodes=sortRealNode1(v,net,currentVNodeId,VNTNodeMappingArray.get(currentVNodeId).possibleRealNodes,selectedRealNodes);
	if(Global.PNodeChoice==2)   /// load balancing
	  VNTNodeMappingArray.get(currentVNodeId).possibleRealNodes=sortRealNode2(v,net,currentVNodeId,VNTNodeMappingArray.get(currentVNodeId).possibleRealNodes,selectedRealNodes);
       for (int i = 0; i < VNTNodeMappingArray.get(currentVNodeId).possibleRealNodes.size() && (mappingFinish == 0)&& (Global.stepsNum<= Global.stepsMax) ; i++)
       {
            /// get next candidate pnode
    	    currentRealNode=Integer.valueOf(VNTNodeMappingArray.get(currentVNodeId).possibleRealNodes.get(i).toString());
           
            try{  /// at each step we have to go back to the initial net
              net = (InPNetwork)ObjectCopier.deepCopy(originalNet);
              }catch(Exception e){System.out.println("exception44///");}
           
           /// if currentRealNode has not been selected before
    	   if(!selectedRealNodes.contains(currentRealNode))
    	     {    	 
    	       int linksMappingRes=1;
               /// start mapping the vlinks connecting the current vnode
       	       for(int k=0;(k<(int)VNTNodeMappingArray.size()&&(linksMappingRes==1));k++)
      	         {
    		   if(VNTNodeMappingArray.get(k).chosenRealNode!=-1)
    		    {
    			/// check if there is virtual link between VNTNodeMappingArray[i].vid and currentVNode
    		     if((v.connections.get(VNTNodeMappingArray.get(k).vid).get(currentVNodeId).id!=-1)||(v.connections.get(currentVNodeId).get(VNTNodeMappingArray.get(k).vid).id!=-1))
    				/// now try to map the virtual link
    			{
    			  // mapping a virtual link affects the availabilty of resources in the network
    			  // so after successfully mapping each virtual link, availability has to be updated before.
    			  //cout<<"matching virtual link : "<<currentVNodeId<<",,"<<VNTNodeMappingArray[i].vid<<endl;
    			  resultedPath = mapVirtualLink(v,net,VNTNodeMappingArray.get(k).vid,currentVNodeId,VNTNodeMappingArray.get(k).chosenRealNode,currentRealNode);    			  
                         if(resultedPath.id==-1) {
                             linksMappingRes=-1; /// stop the loop because the chosen real node is not successfull
                           } 
                           else 
                           {
                	  /// here we change the availability of physical links included in the resultedPath
                	 
                	  linksMappingRes=1;
                	  int requiredLinkCapacity;
                	  if(v.connections.get(VNTNodeMappingArray.get(k).vid).get(currentVNodeId).id!=-1) {
                		  requiredLinkCapacity=v.connections.get(VNTNodeMappingArray.get(k).vid).get(currentVNodeId).capacity;                                  
                		  VNTLinkMappingArray.get(VNTNodeMappingArray.get(k).vid).get(currentVNodeId).resultPath=resultedPath;
                		  VNTLinkMappingArray.get(VNTNodeMappingArray.get(k).vid).get(currentVNodeId).isMapped=1;
                	  }else{
                		  requiredLinkCapacity=v.connections.get(currentVNodeId).get(VNTNodeMappingArray.get(k).vid).capacity;                          
                		  VNTLinkMappingArray.get(currentVNodeId).get(VNTNodeMappingArray.get(k).vid).resultPath=resultedPath;
                		  VNTLinkMappingArray.get(currentVNodeId).get(VNTNodeMappingArray.get(k).vid).isMapped=1;
                	  }
                	  for(int u=0;u<(int)resultedPath.links.size();u++)
                	  {
                            int n1,n2,n3;
                            n1=resultedPath.links.get(u).node1Id;
                            n2=resultedPath.links.get(u).node2Id;
                            if(n2<n1){n3=n2;n2=n1;n1=n3;}
                             net.connections.get(n1).get(n2).capacity=net.connections.get(n1).get(n2).capacity-requiredLinkCapacity;
                	  }

                       }
    		    }
    		}
    	   }
               
    	 if(linksMappingRes==1)   ///mapping of virtual links is done successfully
    	   {
            /// changing the availability of the selected pnode
    	    net.nodes.get(currentRealNode).capacity=net.nodes.get(currentRealNode).capacity-v.vnodes.get(currentVNodeId).capacity;
    	    /// add the current vnode to the mapped vnodes
            mappedVNTNodes.add(currentVNodeId);
            
            /// record the selected pnode
    	    VNTNodeMappingArray.get(currentVNodeId).chosenRealNode=currentRealNode;
            /// add the selected pnode to the set of all selected pnodes
            selectedRealNodes.add(currentRealNode);

    	    /// check if the whole VNT is mapped after this operation
            mappingFinish=1;          
            if(mappedVNTNodes.size()<v.vnodeNum) mappingFinish=0;
            
            
            /// updated the number of connections to the already mapped vnodes
            for(int u=0;u<v.vnodeNum;u++)
            {
            	if((u<currentVNodeId)&&(v.connections.get(u).get(currentVNodeId).id!=-1))
            	{
            	    VNTNodeMappingArray.get(u).connectionsNum++;
            	}else
            	 if((u>currentVNodeId)&&(v.connections.get(currentVNodeId).get(u).id!=-1))
            	   {
            	    VNTNodeMappingArray.get(u).connectionsNum++;
            	   }
             }

    		if((mappingFinish==0)&&(Global.stepsNum<Global.stepsMax))  /// need to continue mapping 
    		     {
                           /// recursive call 
    	      	           mappingFinish=VNTMappingFunc(v,net,selectedRealNodes,VNTNodeMappingArray,VNTLinkMappingArray,sortedVNodesSet,mappedVNTNodes);
    			   if(mappingFinish==0){
    			       /// remove the selected pnode
    			       VNTNodeMappingArray.get(currentVNodeId).chosenRealNode=-1;
    			       selectedRealNodes.remove(currentRealNode);
    			       Global.stepsNum++;
                               //System.out.println("steps = "+Global.stepsNum);
    			       for(int u=0;u<v.vnodeNum;u++)
    			                   {
    			                   	if((u<currentVNodeId)&&(v.connections.get(u).get(currentVNodeId).id!=-1))
    			                   	{
    			                   		VNTNodeMappingArray.get(u).connectionsNum--;
    			                   	}
                                                else
    			                   		if((u>currentVNodeId)&&(v.connections.get(currentVNodeId).get(u).id!=-1))
    			                   		            {
    			                   		            	VNTNodeMappingArray.get(u).connectionsNum--;
    			                   		            }

    			                   }

    			   }
                           
    		     }               
    	     }
         }
     } /// end for loop of possible pnodes ///
    if(mappingFinish==0) {
        try{
            net = (InPNetwork)ObjectCopier.deepCopy(originalNet);
        }catch (Exception e){System.out.println("Exception555/////");}
     }
   }   
      
  if(currentVNodeId==-1) mappingFinish=1;

  return mappingFinish;
 }

 ///// the next method is used to choose the next unmapped vnode  
   public int chooseNextVNode(ArrayList<VNTNodeMappingCell> VNTNodeMappingArray, ArrayList sortedVNodesSet)
   {
       int currentVNodeId = -1;
       for (int i = 0; (i < (int)sortedVNodesSet.size()) && (currentVNodeId == -1); i++)
       {
           int vid = Integer.valueOf(sortedVNodesSet.get(i).toString());
           if (VNTNodeMappingArray.get(vid).chosenRealNode == -1)
               currentVNodeId = vid;
       }

       return currentVNodeId;
   }

 ///// the next method is modified from the above method to select the next virtual node who has the maximum number of connection to the already mapped vnodes
   public int chooseNextVNodeMod(ArrayList<VNTNodeMappingCell> VNTNodeMappingArray)
   {
       int currentVNodeId=-1;
       int temp=-1;
       for (int i = 0; (i < (int)VNTNodeMappingArray.size()); i++)
       {
          //  int vid = Integer.valueOf(VNTNodeMappingArray.get(i).toString());
           if ((VNTNodeMappingArray.get(i).connectionsNum>temp) && (VNTNodeMappingArray.get(i).chosenRealNode == -1))
           {  temp=VNTNodeMappingArray.get(i).connectionsNum;
               currentVNodeId=VNTNodeMappingArray.get(i).vid;
           } 
       }

       return currentVNodeId;
   }
   
   ///// the next method is used to sort the possible real nodes based on the connections number with the selected real nodes.
    ArrayList sortRealNode1(VNTRequest v,InPNetwork net,int currentVNodeId,ArrayList possibleRealNodes,IntSet selectedRealNodes)
       {         
	 int realNode;
	 ArrayList connectionNum=new ArrayList();
         for (int y = 0; y < net.nodeNum;y++ )
          {
            connectionNum.add(0);
          }
     
         Iterator it=selectedRealNodes.iterator(); 
         while(it.hasNext())
           {
            realNode =Integer.valueOf(it.next().toString());	 
            for(int i=0;i<possibleRealNodes.size();i++)
            {
              if ((realNode < Integer.valueOf(possibleRealNodes.get(i).toString())) && (net.connections.get(realNode).get(Integer.valueOf(possibleRealNodes.get(i).toString())).id != -1))
               {
                 int temp=Integer.valueOf(connectionNum.get(Integer.valueOf(possibleRealNodes.get(i).toString())).toString());
                 temp++;
                 connectionNum.remove(Integer.valueOf(possibleRealNodes.get(i).toString()));
                 connectionNum.add(Integer.valueOf(possibleRealNodes.get(i).toString()),temp);
               }
              if ((realNode > Integer.valueOf(possibleRealNodes.get(i).toString())) && (net.connections.get(Integer.valueOf(possibleRealNodes.get(i).toString())).get(realNode).id != -1))
               {
                 int temp=Integer.valueOf(connectionNum.get(Integer.valueOf(possibleRealNodes.get(i).toString())).toString());
                 temp++;
                 connectionNum.remove(Integer.valueOf(possibleRealNodes.get(i).toString()));
                 connectionNum.add(Integer.valueOf(possibleRealNodes.get(i).toString()),temp);
              }
            }
	  }
	
         int j=0;int tmp;
	   for(int i=1;i<(int)possibleRealNodes.size();i++){
	      j=i;
	      tmp=Integer.valueOf(possibleRealNodes.get(i).toString());
	      while (j>0 && Integer.valueOf(connectionNum.get(tmp).toString())>Integer.valueOf(connectionNum.get(Integer.valueOf(possibleRealNodes.get(j-1).toString())).toString()))
	            {
                        int t=Integer.valueOf(possibleRealNodes.get(j-1).toString());
                        possibleRealNodes.remove(j);
                        possibleRealNodes.add(j,t);	    	          
	                j--;
	            }
              possibleRealNodes.remove(j);
              possibleRealNodes.add(j,tmp);	      
	    }
           
     connectionNum.clear();
     return possibleRealNodes;
   }
 
    ArrayList sortRealNode2(VNTRequest v,InPNetwork net,int currentVNodeId,ArrayList possibleRealNodes,IntSet selectedRealNodes)
    {
        //// sort the possible real nodes based on the connections number with the selected real nodes.
	 int realNode;
	 ArrayList capacityDifference=new ArrayList();
	 //capacityDifference.Capacity=net.nodeNum;
	 int requiredCapacity=v.vnodes.get(currentVNodeId).capacity;
        for (int k = 0; k < net.nodeNum;k++ )
        {
            capacityDifference.add(-1);
         }
         for(int i=0;i<possibleRealNodes.size();i++)
         {
             capacityDifference.remove(Integer.valueOf(possibleRealNodes.get(i).toString()));
             capacityDifference.add(Integer.valueOf(possibleRealNodes.get(i).toString()),net.nodes.get(Integer.valueOf(possibleRealNodes.get(i).toString())).capacity-requiredCapacity);
                     
         }


	 ///

	 int j=0;int tmp;
	   for(int i=1;i<(int)possibleRealNodes.size();i++){
	       j=i;
	       tmp=Integer.valueOf(possibleRealNodes.get(i).toString());
               int t1=Integer.valueOf(capacityDifference.get(tmp).toString());
               int t2=Integer.valueOf(possibleRealNodes.get(j-1).toString());
               int t3=Integer.valueOf(capacityDifference.get(t2).toString());
	       while (j>0 && t1>t3)
	            {
                        
                        int t=Integer.valueOf(possibleRealNodes.get(j-1).toString());
                        possibleRealNodes.remove(j);
                        possibleRealNodes.add(j,t);	
                        
	                  j--;
	            }
                     possibleRealNodes.remove(j);
                     possibleRealNodes.add(j,tmp);	  
	        }

	 ///


	  capacityDifference.clear();
	 return possibleRealNodes;
    }
    //// the next method is used to match the capacity and location of a virtual node
    
  
      public ArrayList matchVirtualNode(VNTRequest v, int vid, InPNetwork net)
        {
            ArrayList res = new ArrayList();
            for (int i = 0; i < (int)net.nodes.size(); i++)
            {
                
                if(((v.vnodes.get(vid).pnodeID!="")&&(v.vnodes.get(vid).pnodeID.equals(net.nodes.get(i).pnodeID)))||(v.vnodes.get(vid).pnodeID.equals("-")))
                {                   
                  if ((net.nodes.get(i).capacity >= v.vnodes.get(vid).capacity) )
                    {
                        res.add(i);
                    }                  
                }
            }
            return res;
        }
       
        
         public int matchVirtualNetwork(VNTRequest v,InPNetwork net,ArrayList<ArrayList > res )
         {
                      int t=1;	
                       //// create a set to store the vnodes that have one matched pnodes///
                       IntSet oneMatchedPnodes=new IntSet();
			for(int i=0;(i<(int)v.vnodes.size())&&(t==1);i++){
			   ArrayList s;
                	   s=matchVirtualNode(v,i,net);
               		   if(s.size()==0) t=-1;
                            
                           if (s.size() == 1)
                            {
                              if (oneMatchedPnodes.contains(Integer.parseInt(s.get(0).toString())))
                               {
                                 t = -1; 
                               }
                              else
                             oneMatchedPnodes.add(Integer.parseInt(s.get(0).toString()));
                           }                
                
                           res.add(s);			 
			}
                        
                       
                   return t;
         }        
         
        
        //// the next method is used to release the resources allocated for a VNT
        public static InPNetwork freeVNT(InPNetwork net, MappingResult mappingResult)
        {            
            for (int k = 0; k < mappingResult.nodes.size(); k++)
            {
                net.nodes.get(k).capacity += Integer.valueOf(mappingResult.nodes.get(k).toString());
            }
            for (int k = 0; k < mappingResult.links.size(); k++)
            {
                for (int k2 = 0; k2 < mappingResult.links.get(k).size(); k2++)
                {
                    net.connections.get(k).get(k2).capacity += Integer.valueOf(mappingResult.links.get(k).get(k2).toString());
                }
            }
            return net;
        }

        //// the next method is used to allocate the resources for a VNT based on the mapping result
        public InPNetwork allocateVNT(InPNetwork net, MappingResult mappingResult)
        {
            
            for (int k = 0; k < mappingResult.nodes.size(); k++)
            {
                net.nodes.get(k).capacity -= Integer.valueOf(mappingResult.nodes.get(k).toString());
            }
            for (int k = 0; k < mappingResult.links.size(); k++)
            {
                for (int k2 = 0; k2 < mappingResult.links.get(k).size(); k2++)
                {
                    net.connections.get(k).get(k2).capacity -= Integer.valueOf(mappingResult.links.get(k).get(k2).toString());                
                }
            }
            
            return net;
        }
        
        
        public ArrayList<InPNetwork> releaseAllVNsBeforeCurrentTime(ArrayList<InPNetwork> nets, int currentTime, ArrayList<VNLifetime> VNLifetimes)
        {
            
            for (int i = 0; i < VNLifetimes.size(); i++)
            {
                if (VNLifetimes.get(i).waitingForReleasing == 1)
                {
                    if (VNLifetimes.get(i).releasingTime <= currentTime)
                    {
                        InPNetwork net=new InPNetwork();
                        int providerID=VNLifetimes.get(i).providerID;
                        net=VNTMapper.freeVNT(nets.get(providerID), VNLifetimes.get(i).mres);
                        nets.remove(providerID);
                        nets.add(providerID,net);
                        VNLifetimes.get(i).waitingForReleasing = -1;
                        System.out.println("VNT Released: "+i+"  from provider "+providerID);
                    }
                }
            }
            
            return nets;
        }
        
        public ArrayList<InPNetwork> releaseAllVNs(ArrayList<InPNetwork> nets, List<VNLifetime> VNLifetimes)
        {

            for (int i = 0; i < VNLifetimes.size(); i++)
            {
                if (VNLifetimes.get(i).waitingForReleasing == 1)
                {
                        InPNetwork net=new InPNetwork();
                        int providerID=VNLifetimes.get(i).providerID;
                        net=VNTMapper.freeVNT(nets.get(providerID), VNLifetimes.get(i).mres);
                        nets.remove(providerID);
                        nets.add(providerID,net);
                        
                        VNLifetimes.get(i).waitingForReleasing = -1;
                        System.out.println("VNT Released: "+i+"  from provider "+providerID);
                }
            }

            return nets;
        }

    }

