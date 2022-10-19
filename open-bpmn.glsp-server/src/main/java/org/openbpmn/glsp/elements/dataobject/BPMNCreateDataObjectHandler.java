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
package org.openbpmn.glsp.elements.dataobject;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.server.actions.ActionDispatcher;
import org.eclipse.glsp.server.actions.SelectAction;
import org.eclipse.glsp.server.operations.CreateNodeOperation;
import org.eclipse.glsp.server.utils.GModelUtil;
import org.openbpmn.bpmn.BPMNModel;
import org.openbpmn.bpmn.elements.BPMNActivity;
import org.openbpmn.bpmn.elements.BPMNDataObject;
import org.openbpmn.bpmn.elements.BPMNProcess;
import org.openbpmn.bpmn.exceptions.BPMNModelException;
import org.openbpmn.glsp.bpmn.BpmnPackage;
import org.openbpmn.glsp.elements.CreateBPMNNodeOperationHandler;
import org.openbpmn.model.BPMNGModelState;

import com.google.inject.Inject;

/**
 * The BPMNCreateDataObjectHandler is a GLSP CreateNodeOperation bound to the
 * DiagramModule and called when ever a BPMNDataObject is newly created within
 * the model.
 *
 * @author rsoika
 *
 */
public class BPMNCreateDataObjectHandler extends CreateBPMNNodeOperationHandler {

    private static Logger logger = Logger.getLogger(BPMNCreateDataObjectHandler.class.getName());

    @Inject
    protected BPMNGModelState modelState;

    @Inject
    protected ActionDispatcher actionDispatcher;

    private String elementTypeId;

    /**
     * Default constructor
     * <p>
     * We use this constructor to overwrite the handledElementTypeIds
     */
    public BPMNCreateDataObjectHandler() {
        super(BPMNModel.DATAOBJECT);
    }

    @Override
    protected void executeOperation(final CreateNodeOperation operation) {

        elementTypeId = operation.getElementTypeId();
        // now we add this task into the source model
        String dataObjectID = "dataObject-" + BPMNModel.generateShortID();
        logger.fine("===== > createNode dataObjectID=" + dataObjectID);
        try {
            // find the process - either the default process for Root container or the
            // corresponding participant process
            BPMNProcess bpmnProcess = findProcessByCreateNodeOperation(operation);
            if (bpmnProcess != null) {
                BPMNDataObject dataObject = bpmnProcess.addDataObject(dataObjectID, getLabel());
                Optional<GPoint> point = operation.getLocation();
                if (point.isPresent()) {
                    dataObject.getBounds().setPosition(point.get().getX(), point.get().getY());
                    dataObject.getBounds().setDimension(BPMNActivity.DEFAULT_WIDTH, BPMNActivity.DEFAULT_HEIGHT);

                    logger.info("....Drop Position = " + point.get().getX() + " " + point.get().getY());
                }
            } else {
                // should not happen
                logger.severe("Unable to find a vaild BPMNElement to place the new node: " + elementTypeId);
            }
        } catch (BPMNModelException e) {
            e.printStackTrace();
        }
        modelState.reset();
        actionDispatcher.dispatchAfterNextUpdate(new SelectAction(), new SelectAction(List.of(dataObjectID)));
    }

    @Override
    public String getLabel() {
        int nodeCounter = GModelUtil.generateId(BpmnPackage.Literals.DATA_OBJECT_GNODE, elementTypeId, modelState);
        return "DataObject-" + nodeCounter;
    }

}
