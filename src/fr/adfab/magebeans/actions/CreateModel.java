/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.adfab.magebeans.actions;

import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "MageBeans/SubActions",
        id = "fr.adfab.magebeans.actions.CreateModel"
)
@ActionRegistration(
        displayName = "#CTL_CreateModel"
)
@ActionReferences({
    @ActionReference(path = "Projects/Actions")
})
@Messages("CTL_CreateModel=Create model")
public final class CreateModel extends AbstractAction implements ActionListener{

    private final List context;
    
    public CreateModel(List context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        JOptionPane.showMessageDialog(null, context.size() + " projects selected: " + context);
    }
}
