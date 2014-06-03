package org.jcryptool.visual.crtverification.views;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class ChooseCertPage extends WizardPage {
   	private ChooseCertComposite compositeFile;
    int certType; // [1] UserCert; [2] Cert; [3] RootCert
    
    public int getCertType() {
		return certType;
	}


	public void setCertType(int certType) {
		this.certType = certType;
	}
    
    public ChooseCertPage(String pageName, int type) {
        super(pageName);
        certType = type;
        setTitle(pageName);
        setDescription("Bitte w\u00e4hlen Sie ein zu ladendes Zertifikat aus dem Java Keystore aus.");
    }
    
    
    // TestComposite wird erzeugt
    public void createControl(Composite parent) {
        setPageComplete(false);
        compositeFile = new ChooseCertComposite(parent, NONE, this);
        setControl(compositeFile);
    }

    /**
     * @return the compositeFile
     */
    public ChooseCertComposite getCompositeFile() {
        return compositeFile;
    }

}
