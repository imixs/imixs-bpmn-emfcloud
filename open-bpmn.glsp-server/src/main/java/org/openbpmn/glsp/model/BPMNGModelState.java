/********************************************************************************
 * Copyright (c) 2022 Imixs Software Solutions GmbH and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.openbpmn.glsp.model;

import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.glsp.server.model.DefaultGModelState;
import org.openbpmn.bpmn.BPMNModel;
import org.openbpmn.bpmn.exceptions.BPMNModelException;
import org.w3c.dom.Document;

/**
 * The BPMNGModelState extends the DefaultGModelState and provides the property
 * 'bpmnModel' which holds an instance of the BPMN MetaModel.
 * <p>
 * The BPMNModelState also holds a revision stack of all model updates to
 * support the undo/redo actions that are triggered by ....?
 * 
 * 
 * @author rsoika
 * @version 1.0
 */
public class BPMNGModelState extends DefaultGModelState {

    private static Logger logger = LogManager.getLogger(BPMNGModelState.class);

    private BPMNModel bpmnModel;

    private Stack<Document> undoStack = null;
    private Stack<Document> redoStack = null;

    private boolean initialized = false;
    private String rootID = "undefined_root_id";

    public BPMNGModelState() {
        undoStack = new Stack<Document>();
        redoStack = new Stack<Document>();
    }

    public BPMNModel getBpmnModel() {
        return bpmnModel;
    }

    public void setBpmnModel(final BPMNModel bpmnModel) {
        this.bpmnModel = bpmnModel;
        // create a new unique id
        rootID = "root_" + BPMNModel.generateShortID();
        this.setRoot(null);
    }

    // @Override
    // public void updateRoot(GModelRoot newRoot) {
    // super.updateRoot(newRoot);
    // }

    /**
     * Helper method to store the current model revision on the revisions stack.
     * 
     */
    public void storeRevision() {
        logger.info("...store revision " + this.getRoot().getRevision());
        long l = System.currentTimeMillis();
        Document doc = bpmnModel.getDoc();

        Document doc2 = (Document) doc.cloneNode(true);

        logger.debug("...clone took " + (System.currentTimeMillis() - l) + "ms");

        undoStack.push(doc2);

    }

    @Override
    public boolean canUndo() {
        return (undoStack.size() > 1);
    }

    @Override
    public boolean canRedo() {
        return (redoStack.size() > 0);
    }

    /**
     * This method fetches the latest revision from the undoStack and updates the
     * current bpmnModel instance.
     * The method also pushes the latest version on the redoStack to support redo
     * actions as well.
     */
    @Override
    public void undo() {
        // we need to take 2 revisions from the stack!
        logger.debug("start undo - current stack size=" + undoStack.size());
        Document doc = undoStack.pop(); // current version
        // push current version to redoStack
        redoStack.push(doc);
        doc = undoStack.pop(); // previous version
        try {
            bpmnModel = new BPMNModel(doc);
            this.getRoot().setRevision(getRoot().getRevision() - 2);
        } catch (BPMNModelException e) {
            logger.warn("unable to undo model changes: " + e.getMessage());
            e.printStackTrace();
        }
        reset();
        super.undo();
    }

    /**
     * This method fetches the latest version from the redoStack and updates the
     * current bpmnModel instance.
     */
    @Override
    public void redo() {
        // we need to take 2 revisions from the stack!
        logger.debug("start redo - current stack size=" + redoStack.size());
        Document doc = redoStack.pop();

        try {
            bpmnModel = new BPMNModel(doc);
            this.getRoot().setRevision(getRoot().getRevision() - 1);
        } catch (BPMNModelException e) {
            logger.warn("unable to undo model changes: " + e.getMessage());
            e.printStackTrace();
        }
        reset();
        super.redo();
    }

    public String getRootID() {
        return rootID;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Reset the model state which will force a re-generation in the
     * BPMNGModelFactory.createGModel() method
     */
    public void reset() {
        this.initialized = false;
    }

}
