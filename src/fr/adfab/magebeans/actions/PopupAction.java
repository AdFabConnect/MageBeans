/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.adfab.magebeans.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "MageBeans/SubActions",
        id = "fr.adfab.magebeans.actions.PopupAction"
)
@ActionRegistration(
        displayName = "#CTL_PopupAction", lazy = false
)
@ActionReferences({
    @ActionReference(path = "Projects/Actions")
})
@Messages("CTL_PopupAction=MageBeans")
public final class PopupAction extends AbstractAction implements ActionListener, Presenter.Popup {

    public PopupAction() {
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu main = new JMenu(Bundle.CTL_PopupAction());
        List<? extends Action> actionsForPath;
        actionsForPath = Utilities.actionsForPath("Actions/MageBeans/SubActions");
        actionsForPath.forEach((action) -> {
            main.add(action);
        });
        return main;
    }
}
