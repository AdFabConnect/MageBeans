/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.adfab.magebeans.processes;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

import org.openide.util.Exceptions;
/**
 *
 * @author vanthiepnguyen
 */
public class CreateModuleProcess {
    final String PATH_TEMP;
    final String CONFIG_PATH;
    final String MODULE_CONFIG_PATH;

    protected String codePool = "local";
    protected String projectDir = "";
    protected String moduleDir = "";
    protected String company = "Company";
    protected String module = "Module";
    protected String moduleName = "";
    protected String moduleNameLower = "";
    protected boolean hasBlock = false;
    protected boolean hasModel = false;
    protected boolean hasHelper = false;
    protected boolean hasSetup = false;

    protected Document dom;
    protected Element configElement;
    protected Element globalElement;

    public CreateModuleProcess() {
        this.PATH_TEMP = "%s/app/code/%s/%s/%s";
        this.CONFIG_PATH = "etc/config.xml";
        this.MODULE_CONFIG_PATH = "%s/app/etc/modules/%s_%s.xml";
    }

    public void setCompany(String company) {
        Pattern p = Pattern.compile("^[A-Z][a-z0-9]*$");
        Matcher m = p.matcher(company);
        if (m.matches()) {
            this.company = company;
        } else {
            throw new IllegalArgumentException(String.format("'%s' is not good format", company));
        }
    }

    public void setModule(String module) {
        Pattern p = Pattern.compile("^[A-Z][a-z0-9]*$");
        Matcher m = p.matcher(module);
        if (m.matches()) {
            this.module = module;
        } else {
            throw new IllegalArgumentException(String.format("'%s' is not good format", module));
        }
    }


    public void setCodePool(String codePool) {
        if ("local".equals(codePool) || "community".equals(codePool)) {
            this.codePool = codePool;
        } else {
            throw new IllegalArgumentException(String.format("Code pool '%s' does not allow", codePool));
        }
    }

    public void setProjectDir(String projectDir) throws FileNotFoundException {
        File tmp = new File(projectDir);
        if (!tmp.exists()) {
            throw new FileNotFoundException("Project directory not found");
        }
        this.projectDir = projectDir;
    }

    public void setHasBlock(boolean hasBlock) {
        this.hasBlock = hasBlock;
    }

    public void setHasModel(boolean hasModel) {
        this.hasModel = hasModel;
    }

    public void setHasHelper(boolean hasHelper) {
        this.hasHelper = hasHelper;
    }

    public void setHasSetup(boolean hasSetup) {
        this.hasSetup = hasSetup;
    }


    public void process() throws FileNotFoundException {
        if ("".equals(this.projectDir)) {
            throw new FileNotFoundException("Project directory not found");
        }
        //Prepare some variables
        this.moduleName = this.company + "_" + this.module;
        this.moduleNameLower = this.moduleName.toLowerCase();
        this.moduleDir = String.format(this.PATH_TEMP, this.projectDir, this.codePool, this.company, this.module);

        // Create config folder
        this._createFolders("etc");
        this._createModuleXmlFile();

        this._createConfigXml();
        this._writeConfigXmlFile();
    }


    protected void _createConfigXml() {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.dom = documentBuilder.newDocument();
            this.configElement = this.dom.createElement("config");
            this.globalElement = this.dom.createElement("global");

            // Create config for module version
            Element moduleElement = this.dom.createElement(this.moduleName);
            Element versionElement = this.dom.createElement("version");
            versionElement.setTextContent("0.1.0");
            moduleElement.appendChild(versionElement);
            this.configElement.appendChild(moduleElement);

            if (this.hasBlock) {
                this._createConfigNode("block");
                this._createFolders("Block");
            }

            if (this.hasModel) {
                this._createConfigNode("model");
                this._createFolders("Model");
            }

            if (this.hasHelper) {
                this._createConfigNode("helper");
                this._createFolders("Helper");
            }

            if (this.hasSetup) {
                this._createSetup();
            }

            this.configElement.appendChild(this.globalElement);
            this.dom.appendChild(this.configElement);
        } catch (ParserConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Create config file Company_Module.xml in app/etc/modules
     */
    protected void _createModuleXmlFile() {
        try {
            String fileContent = this._getResourceFile("module.xml");
            fileContent = String.format(fileContent, this.moduleName, this.codePool, this.moduleName);
            String fileConfigPath = String.format(this.MODULE_CONFIG_PATH, this.projectDir, this.company, this.module);
            File fileConfig = new File (fileConfigPath);
            try (FileOutputStream fos = new FileOutputStream(fileConfig)) {
                if (!fileConfig.exists()) {
                    fileConfig.createNewFile();
                }
                fos.write(fileContent.getBytes());
                fos.flush();
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }

    protected void _createSetup() {
        String xpath = "resources/" + this.moduleNameLower + "_setup/setup/module";
        this._createNode(xpath, this.moduleName);
        xpath = "resources/" + this.moduleNameLower + "_setup/connection/use";
        this._createNode(xpath, "core_setup");
        String subDir = "sql/" + this.moduleNameLower + "_setup";
        this._createFolders(subDir);
        
        String setupFileName = subDir + "/mysql4-install-0.1.0.php";
        File setupFile = new File(setupFileName);
        String sourceFile = this._getResourceFile("mysql4-install-0.1.0.php");
        try {
            FileOutputStream fos = new FileOutputStream(setupFile);
            if (!setupFile.exists()) {
                setupFile.createNewFile();
            }
            fos.write(sourceFile.getBytes());
            fos.flush();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }

    protected String _getResourceFile(String resourceFilePath) {
        InputStream templateFile;
        templateFile = getClass().getResourceAsStream("resourceFilePath");
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(templateFile, writer, "UTF-8");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        String fileContent = writer.toString();
        return fileContent;
    }

    protected void _writeConfigXmlFile() {
        try {
            String configFile = this.moduleDir + "/" + this.CONFIG_PATH;
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            tr.transform(new DOMSource(dom),
                    new StreamResult(new FileOutputStream(configFile)));
        } catch (TransformerConfigurationException ex) {
            System.out.println(ex.getMessage());
        } catch (TransformerException | FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    protected void _createNode(String xpath, String value) {
        String[] nodes = xpath.split("/");
        Element parent = this.globalElement;
        for (String node : nodes) {
            NodeList nodeList = parent.getElementsByTagName(node);
            if (nodeList == null || nodeList.getLength() == 0) {
                Element newElement = this.dom.createElement(node);
                parent.appendChild(newElement);
                parent = newElement;
            } else {
                parent = (Element) nodeList.item(0);
            }
        }
        parent.setTextContent(value);
    }

    protected void _createConfigNode(String type) {
        String types = type + "s";
        String xpath = types + "/" + this.moduleNameLower + "/class";
        this._createNode(xpath, this.moduleName + "_" + Utils.capitalize(type));
    }
    /**
     * Create module folders
     */
    protected void _createFolders(String subDir) {
        String dir;
        dir = this.moduleDir + "/" + subDir;
        File tmp = new File(dir);
        tmp.mkdirs();
    }
}