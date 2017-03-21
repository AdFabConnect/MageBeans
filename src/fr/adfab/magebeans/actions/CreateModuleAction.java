package fr.adfab.magebeans.actions;

import fr.adfab.magebeans.guis.CreateModuleForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JOptionPane;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

@ActionID(
        category = "MageBeans",
        id = "fr.adfab.magebeans.actions.CreateModuleAction"
)
@ActionRegistration(
        iconBase = "fr/adfab/magebeans/actions/icon.png",
        displayName = "Create Magento Module"
)
@ActionReferences({
    @ActionReference(path = "Menu/Source", position = 200, separatorAfter = 250)
    ,
  @ActionReference(path = "Toolbars/File", position = 500)
})

public final class CreateModuleAction implements ActionListener {

    private final Project project;

    public CreateModuleAction(Project context) {
        this.project = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        FileObject projectDir = project.getProjectDirectory();
        String filePath = projectDir.getPath() + "/app/Mage.php";
        File mageFile = new File(filePath);
        if (mageFile.exists() && !mageFile.isDirectory()) {
            String args[] = new String[1];
            args[0] = projectDir.getPath();
            CreateModuleForm.main(args);
        } else {
            JOptionPane.showMessageDialog(null, "Current project is not magento 1x", "MageBeans plugin", JOptionPane.ERROR_MESSAGE);
        }
    }
}
