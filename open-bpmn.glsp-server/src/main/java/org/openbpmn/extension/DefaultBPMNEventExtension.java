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
package org.openbpmn.extension;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import org.eclipse.glsp.graph.GModelElement;
import org.openbpmn.bpmn.BPMNTypes;
import org.openbpmn.bpmn.elements.Event;
import org.openbpmn.bpmn.elements.core.BPMNElement;
import org.openbpmn.glsp.jsonforms.DataBuilder;
import org.openbpmn.glsp.jsonforms.SchemaBuilder;
import org.openbpmn.glsp.jsonforms.UISchemaBuilder;
import org.openbpmn.glsp.jsonforms.UISchemaBuilder.Layout;

/**
 * This is the Default BPMNEvent extension providing the JSONForms schemata.
 *
 * @author rsoika
 *
 */
public class DefaultBPMNEventExtension extends AbstractBPMNElementExtension {

    public DefaultBPMNEventExtension() {
        super();
    }

    /**
     * Returns if this Extension can be applied to the given elementTypeID
     */
    @Override
    public boolean handlesElementTypeId(final String elementTypeId) {
        return BPMNTypes.BPMN_EVENTS.contains(elementTypeId);
    }

    /**
     * This Extension is for BPMNEvents only
     */
    @Override
    public boolean handlesBPMNElement(final BPMNElement bpmnElement) {
        return (bpmnElement instanceof Event);
    }

    /**
     * This Helper Method generates a JSONForms Object with the BPMNElement
     * properties.
     * <p>
     * This JSON object is used on the GLSP Client to generate the EMF JsonForms
     * <p>
     * The method iterates over all eventDefinitions and adds a separate tab
     * including definitions of this type. Note: an event can have multiple
     * definitions of the same type.
     */
    @Override
    public void buildPropertiesForm(final BPMNElement bpmnElement, final DataBuilder dataBuilder,
            final SchemaBuilder schemaBuilder, final UISchemaBuilder uiSchemaBuilder) {

        dataBuilder. //
                addData("name", bpmnElement.getName()). //
                addData("documentation", bpmnElement.getDocumentation());

        schemaBuilder. //
                addProperty("name", "string", null). //
                addProperty("documentation", "string", null);

        Map<String, String> multilineOption = new HashMap<>();
        multilineOption.put("multi", "true");
        uiSchemaBuilder. //
                addCategory("General"). //
                addLayout(Layout.VERTICAL). //
                addElements("name"). //
                addElement("documentation", "Documentation", multilineOption);

    }

    /**
     * Updates the core attributes of the BPMN element and also the
     * eventDefinitions based on the different property tabs for each definition
     * type.
     */
    @Override
    public void updatePropertiesData(final JsonObject json, final String category, final BPMNElement bpmnElement,
            final GModelElement gNodeElement) {

        // we are only interested in category general
        if (!"General".equals(category)) {
            return;
        }

        updateNameProperty(json, bpmnElement, gNodeElement);
        // update attributes and tags
        bpmnElement.setDocumentation(json.getString("documentation", ""));

    }
}
