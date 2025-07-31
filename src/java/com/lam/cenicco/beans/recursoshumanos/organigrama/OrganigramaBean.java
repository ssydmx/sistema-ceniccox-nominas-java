/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.recursoshumanos.organigrama;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Antonio Durán
 */
@Named(value = "organigramaBean")
@SessionScoped
public class OrganigramaBean implements Serializable {

    private TreeNode root;
//    
    private TreeNode singleSelectedTreeNode;

    @PostConstruct
    public void init() {
        System.out.println("Construccion de Arbol... ");
        root = new DefaultTreeNode("Root", null);
//        
        TreeNode node0 = new DefaultTreeNode("Node 0", root);
        TreeNode node1 = new DefaultTreeNode("Node 1", root);

        TreeNode node00 = new DefaultTreeNode("Node 0.0", node0);
        TreeNode node01 = new DefaultTreeNode("Node 0.1", node0);

        TreeNode node10 = new DefaultTreeNode("Node 1.0", node1);

        node1.getChildren().add(new DefaultTreeNode("Node 1.1"));
        node00.getChildren().add(new DefaultTreeNode("Node 0.0.0"));
        node00.getChildren().add(new DefaultTreeNode("Node 0.0.1"));
        node01.getChildren().add(new DefaultTreeNode("Node 0.1.0"));
        node10.getChildren().add(new DefaultTreeNode("Node 1.0.0"));
//        
        root.getChildren().add(new DefaultTreeNode("Node 2"));
    }

    public void onNodeSelect(NodeSelectEvent event) {
        System.out.println("Node Data ::" + event.getTreeNode().getData() + " :: Selected");
    }

    public void onNodeUnSelect(NodeUnselectEvent event) {
        System.out.println("Node Data ::" + event.getTreeNode().getData() + " :: UnSelected");
    }

    public void onNodeExpand(NodeExpandEvent event) {
        System.out.println("Node Data ::" + event.getTreeNode().getData() + " :: Expanded");
    }

    public void onNodeCollapse(NodeCollapseEvent event) {
        System.out.println("Node Data ::" + event.getTreeNode().getData() + " :: Collapsed");
    }
//    public void displaySelectedMultiple() {
//        if (this.selectedNodes != null && this.selectedNodes.length > 0) {
//            StringBuilder builder = new StringBuilder();
//
//            for (TreeNode node : this.selectedNodes) {
//                builder.append(node.getData().toString());
//                builder.append("<br />");
//            }
//
//            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", builder.toString());
//            FacesContext.getCurrentInstance().addMessage(null, message);
//        }
//    }

    public TreeNode getRoot() {
        return root;
    }

    public TreeNode getSingleSelectedTreeNode() {
        return singleSelectedTreeNode;
    }

    public void setSingleSelectedTreeNode(TreeNode singleSelectedTreeNode) {
        this.singleSelectedTreeNode = singleSelectedTreeNode;
    }
}
