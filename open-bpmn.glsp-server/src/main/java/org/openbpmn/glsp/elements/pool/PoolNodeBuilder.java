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
package org.openbpmn.glsp.elements.pool;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GLabel;
import org.eclipse.glsp.graph.builder.AbstractGNodeBuilder;
import org.eclipse.glsp.graph.builder.impl.GCompartmentBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.util.GConstants;
import org.openbpmn.bpmn.elements.BPMNParticipant;
import org.openbpmn.glsp.bpmn.BpmnFactory;
import org.openbpmn.glsp.bpmn.Pool;
import org.openbpmn.glsp.utils.ModelTypes;

/**
 * BPMN 2.0 Pool Element
 *
 * @author rsoika
 *
 */
public class PoolNodeBuilder extends AbstractGNodeBuilder<Pool, PoolNodeBuilder> {
    private String name;

    private static final String V_GRAB = "vGrab";
    private static final String H_GRAB = "hGrab";
    private static final String H_ALIGN = "hAlign";

    public PoolNodeBuilder(final String type, final String name) {
        super(type);
        this.name = name;
        this.addCssClass("pool");
        this.addCssClass(type);

    }

    @Override
    protected Pool instantiate() {
        return BpmnFactory.eINSTANCE.createPool();
    }

    @Override
    protected PoolNodeBuilder self() {
        return this;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public void setProperties(final Pool node) {
        super.setProperties(node);
        node.setName(name);

        node.setLayout(GConstants.Layout.VBOX);
        // Set min width/height
        node.getLayoutOptions().put(GLayoutOptions.KEY_MIN_WIDTH, BPMNParticipant.DEFAULT_WIDTH);
        node.getLayoutOptions().put(GLayoutOptions.KEY_MIN_HEIGHT, BPMNParticipant.DEFAULT_HEIGHT);

        node.getLayoutOptions().put(GLayoutOptions.KEY_H_GAP, 10);
        node.getLayoutOptions().put(GLayoutOptions.KEY_V_ALIGN, "center");

        node.getChildren().add(createLabelCompartment(node));
        node.getChildren().add(createStructCompartment(node));

    }

    private GCompartment createLabelCompartment(final Pool node) {
        Map<String, Object> layoutOptions = new HashMap<>();

        return new GCompartmentBuilder(ModelTypes.COMP_HEADER) //
                .id(node.getId() + "_header") //
                .layout(GConstants.Layout.HBOX) //
                .layoutOptions(layoutOptions) //
                .add(createCompartmentHeader(node)) //
                .build();
    }

    private GLabel createCompartmentHeader(final Pool node) {
        return new GLabelBuilder(ModelTypes.LABEL_HEADING) //
                .id(node.getId() + "_header_label") //
                .text(node.getName()) //
                .build();
    }

    private GCompartment createStructCompartment(final Pool node) {
        Map<String, Object> layoutOptions = new HashMap<>();
        layoutOptions.put(H_ALIGN, "left");
        layoutOptions.put(H_GRAB, true);
        layoutOptions.put(V_GRAB, true);
        GCompartmentBuilder structCompartmentBuilder = new GCompartmentBuilder(ModelTypes.STRUCTURE) //
                .id(node.getId() + "_struct") //
                .layout(GConstants.Layout.FREEFORM) //
                .layoutOptions(layoutOptions);
        return structCompartmentBuilder //
                .build();
    }
}
