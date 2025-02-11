// -----BEGIN DISCLAIMER-----
/*******************************************************************************
 * Copyright (c) 2019 JCrypTool Team and Contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
// -----END DISCLAIMER-----
package org.jcryptool.analysis.freqanalysis.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jcryptool.analysis.freqanalysis.FreqAnalysisPlugin;
import org.jcryptool.analysis.freqanalysis.calc.FreqAnalysisCalc;
import org.jcryptool.core.logging.utils.LogUtil;
import org.jcryptool.core.operations.alphabets.AbstractAlphabet;
import org.jcryptool.core.operations.alphabets.AlphabetsManager;
import org.jcryptool.core.util.ui.SingleVanishTooltipLauncher;

import com.cloudgarden.resource.SWTResourceManager;

/**
 * @author SLeischnig
 *
 */
public class FullAnalysisUI extends AbstractAnalysisUI {

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	private Button button1;
	private Composite composite1;
	private Group group1;
	private Group group4;
	private Composite composite2;
	private Button button4;
	private Group group2;
	private Label label1;
	private TabItem tabItem2;
	private Button btnReferenceTools;
	private Composite composite4;
	private Composite composite3;
	private TabItem tabItem1;
	private TabFolder tabFolder1;
	private Combo combo1;
	private Combo combo2;
	private Label label3;
	private Label label2;

	private FreqAnalysisCalc myAnalysis;
	private FreqAnalysisCalc overlayAnalysis;
	private String myOverlayAlphabet = ""; //$NON-NLS-1$
	private String reftext;
	private Vector<Reftext> reftexts;
	private FreqAnalysisCalc myLimitedAnalysis;
	private SingleVanishTooltipLauncher tipLauncher;
	private boolean appropriateAlphabetToBeDetected = false;

	/**
	 * Contains reference texts for overlays
	 *
	 * @author SLeischnig
	 *
	 */
	private class Reftext {
		private String path, text, name;

		public Reftext(final String name, final String path) {
			this.name = name;
			this.path = path;
			text = InputStreamToString(openMyTestStream(this.path));
		}

		// public String getName() {return name;}
		public String getText() {
			return text;
		}
		// public String getPath() {return path;}
	}

	public FullAnalysisUI(final Composite parent, final int style) {
		super(parent, style);
		reftexts = new Vector<Reftext>(0, 1);

		initGUI();
		changedVigOptions();
		combo1.select(0);
		combo1WidgetSelected(null);
		hideObject(group2, button3.getSelection());
	}

	private void initGUI() {
		setLayout(new GridLayout());

		button1 = new Button(this, SWT.PUSH | SWT.CENTER);
		button1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button1.setText(Messages.FullAnalysisUI_startanalysis);
		button1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent evt) {
				if (checkEditor()) {
					text = getEditorText();
					if (btnReferenceTools.getSelection()) {
						selectAppropriateAlphabet();
					} else {
						appropriateAlphabetToBeDetected = true;
					}
					recalcGraph();
				}
			}
		});

		composite1 = new Composite(this, SWT.NONE);
		composite1.setLayout(new GridLayout(2, false));
		composite1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		group1 = new Group(composite1, SWT.NONE);
		group1.setLayout(new GridLayout());
		group1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group1.setText(Messages.FullAnalysisUI_graphlabel);

		myGraph = new CustomFreqCanvas(group1, SWT.NONE);
		myGraph.setLayout(new GridLayout());
		myGraph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		group4 = new Group(composite1, SWT.NONE);
		group4.setLayout(new GridLayout());
		group4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		group4.setText(Messages.FullAnalysisUI_propertieslabel);

		tabFolder1 = new TabFolder(group4, SWT.NONE);

		tabItem1 = new TabItem(tabFolder1, SWT.NONE);
		tabItem1.setText(Messages.FullAnalysisUI_firsttablabel);

		composite3 = new Composite(tabFolder1, SWT.NONE);

		composite3.setLayout(new GridLayout());
		composite3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		tabItem1.setControl(composite3);

		composite2 = new Composite(composite3, SWT.NONE);
		composite2.setLayout(new GridLayout());
		composite2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		button3 = new Button(composite2, SWT.RADIO);
		button3.setText(Messages.FullAnalysisUI_monoalphabetic);
		button3.setSelection(true);
		button3.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent evt) {
				polyOnOffSelected(evt);
			}
		});

		button4 = new Button(composite2, SWT.RADIO);
		button4.setText(Messages.FullAnalysisUI_polyalphabetic);
		button4.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent evt) {
				polyOnOffSelected(evt);
			}
		});

		group2 = new Group(composite3, SWT.NONE);
		group2.setLayout(new GridLayout(2, false));

		group2.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		group2.setText(Messages.FullAnalysisUI_vigeneresettings);

		spinner1 = new Spinner(group2, SWT.BORDER);
		spinner1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		spinner1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent evt) {
				recalcGraph();
			}
		});

		spinner1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent evt) {
				changedVigOptions();
				recalcGraph();
			}
		});

		spinner1.setSelection(1);

		label1 = new Label(group2, SWT.NONE);
		label1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		label1.setText(Messages.FullAnalysisUI_keylength);

		spinner2 = new Spinner(group2, SWT.BORDER);
		spinner2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		spinner2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent evt) {
				recalcGraph();
			}
		});

		spinner2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				changedVigOptions();
			}
		});

		label2 = new Label(group2, SWT.NONE);
		label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		label2.setText(Messages.FullAnalysisUI_keyoffset);

		tabItem2 = new TabItem(tabFolder1, SWT.NONE);
		tabItem2.setText(Messages.FullAnalysisUI_secondtablabel);

		composite4 = new Composite(tabFolder1, SWT.NONE);
		composite4.setLayout(new GridLayout());

		tabItem2.setControl(composite4);

		btnReferenceTools = new Button(composite4, SWT.CHECK);
		btnReferenceTools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		btnReferenceTools.setText(Messages.FullAnalysisUI_enabledecrOverlay);
		btnReferenceTools.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent evt) {

				//Enable deciphering 
				if (!btnReferenceTools.getSelection()) {
					if (checkEditor()) {
						enableReferenceTools(true);
						if (myLimitedAnalysis == null) {
						recalcGraph();
						}
						if (myLimitedAnalysis != null) {
							myGraph.setAnalysis(myLimitedAnalysis);
						}
						if (myAnalysis != null || myLimitedAnalysis != null) {
							myGraph.setOverlayActivated(!btnReferenceTools.getSelection());
							myGraph.redraw();
						}
					}
				} else {
					//disable deciphering
					enableReferenceTools(false);
					if (myAnalysis == null) {
						recalcGraph();
					}
					if (myAnalysis != null) {
						myGraph.setAnalysis(myAnalysis);
					}
					if (myAnalysis != null || myLimitedAnalysis != null) {
						myGraph.setOverlayActivated(!btnReferenceTools.getSelection());
						myGraph.redraw();
					}
				}
//				if (myAnalysis != null || myLimitedAnalysis != null) {
//					myGraph.setOverlayActivated(!btnReferenceTools.getSelection());
//					myGraph.redraw();
//				}
			}
		});

		combo1 = new Combo(composite4, SWT.READ_ONLY);
		combo1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		// add reftexts
		reftexts.add(new Reftext(Messages.FullAnalysisUI_germanreftextname1, Messages.FullAnalysisUI_0));
		reftexts.add(new Reftext(Messages.FullAnalysisUI_germanreftextname2, Messages.FullAnalysisUI_1));
		reftexts.add(new Reftext(Messages.FullAnalysisUI_englishreftextname1, Messages.FullAnalysisUI_2));
		reftexts.add(new Reftext(Messages.FullAnalysisUI_frenchreftextname1, Messages.FullAnalysisUI_3));

		for (int i = 0; i < reftexts.size(); i++) {
			combo1.add(reftexts.get(i).name);
		}

		combo1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent evt) {
				combo1WidgetSelected(evt);
			}
		});

		combo1.setText(combo1.getItem(0));

		label3 = new Label(composite4, SWT.NONE);
		label3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		label3.setText(Messages.FullAnalysisUI_alphabetused);

		combo2 = new Combo(composite4, SWT.READ_ONLY);
		combo2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		combo2.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent evt) {
				combo2WidgetSelected(evt);
			}
		});

		AbstractAlphabet[] alphas = AlphabetsManager.getInstance().getAlphabets();
		for (int i = 0; i < alphas.length; i++) {
			combo2.add(alphas[i].getName());
			if (alphas[i].isDefaultAlphabet()) {
				// combo2.setText(alphas[i].getName());
				combo2.select(i);
				myOverlayAlphabet = String.valueOf(alphas[i].getCharacterSet());
			}
		}

		tabFolder1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tabFolder1.setSelection(0);

		layout();

		tipLauncher = new SingleVanishTooltipLauncher(getShell());
		enableReferenceTools(false);
	}

	protected void enableReferenceTools(boolean b) {
		combo1.setEnabled(b);
		combo2.setEnabled(b);
		label3.setEnabled(b);
		if (appropriateAlphabetToBeDetected) {
			selectAppropriateAlphabet();
			appropriateAlphabetToBeDetected = false;
		}
//		 btnReferenceTools.setSelection(b);
	}

	/**
	 * opens a resource file stream
	 *
	 * @param filename
	 *            the file path
	 * @return the inputStream containing the file's content
	 */
	private InputStream openMyTestStream(final String filename) {
		try {
			URL installURL = FreqAnalysisPlugin.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
			URL url = new URL(installURL, filename);
			return (url.openStream());
		} catch (MalformedURLException e) {
			LogUtil.logError(FreqAnalysisPlugin.PLUGIN_ID, e);
		} catch (IOException e) {
			LogUtil.logError(FreqAnalysisPlugin.PLUGIN_ID, e);
		}
		return null;
	}

	/**
	 * excludes a control from the Layout calculation
	 *
	 * @param that
	 * @param hideit
	 */
	private void hideObject(final Control that, final boolean hideit) {
		GridData GData = (GridData) that.getLayoutData();
		GData.exclude = true && hideit;
		that.setVisible(true && !hideit);
		Control[] myArray = { that };
		layout(myArray);
	}

	@Override
	protected void analyze() {
		myAnalysis = new FreqAnalysisCalc(text, myLength, myOffset, null);
		myLimitedAnalysis = new FreqAnalysisCalc(text, myLength, myOffset, null, myOverlayAlphabet);
		if (btnReferenceTools.getSelection())
			myGraph.setAnalysis(myLimitedAnalysis);
		else
			myGraph.setAnalysis(myAnalysis);
	}

	/**
	 * executes the necessary calculations when Vigenère parameters were changed
	 */
	private void changedVigOptions() {
		if (spinner1.getSelection() < 1) {
			spinner1.setSelection(1);
		}
		if (spinner2.getSelection() < 0) {
			spinner2.setSelection(0);
		}
		spinner1.setMinimum(1);
		spinner1.setMaximum(999999);
		spinner2.setMinimum(0);
		spinner2.setMaximum(spinner1.getSelection() - 1);
		if (spinner2.getSelection() >= spinner1.getSelection()) {
			spinner2.setSelection(spinner1.getSelection() - 1);
		}
		spinner2.setMaximum(spinner1.getSelection());
		myGraph.getFrequencyGraph().resetDrag();
	}

	/**
	 * removes a specific character from a string
	 *
	 * @param s
	 *            the string
	 * @param c
	 *            the character
	 */
	private String removeChar(final String s, final char c) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != c) {
				result.append(s.charAt(i));
			}
		}
		return result.toString();
	}

	/**
	 * Returns a String which contains each character and control sequence the text
	 * contains only and only once.
	 */
	private String countDifferentChars(final String text) {

		int i = 0;
		String myText = text;
		while (i < myText.length()) {
			myText = myText.substring(0, i + 1).concat(removeChar(myText.substring(i + 1), myText.charAt(i)));
			i++;
		}
		return myText;
	}

	/**
	 * a rating function; calculates a value for how much an alphabet is fitting a
	 * text.
	 *
	 * @param alphabet
	 *            the alphabets (each character only to be contained once!)
	 * @param text
	 *            the text
	 * @return the rating. Higher values are better. Rating zero is best.
	 */
	private double rateAlphabetTextDifference(final String alphabet, final String text) {
		String condensedText = countDifferentChars(text);
		double penaltyTextNotInAlphabet = (double) (condensedText.length()) / 4; // Its really bad
																					// if one character
																					// from the text
																					// should not
																					// appear in the
																					// alphabet..
		double penaltyAlphabetNotInText = (double) (alphabet.length()) / 25;

		int rating = 0;
		for (int i = 0; i < condensedText.length(); i++) {
			if (!alphabet.contains(String.valueOf(condensedText.charAt(i)))) {
				rating -= penaltyTextNotInAlphabet;
			}
		}

		for (int i = 0; i < alphabet.length(); i++) {
			if (!condensedText.contains(String.valueOf(alphabet.charAt(i)))) {
				rating -= penaltyAlphabetNotInText;
			}
		}

		return rating;
	}

	/**
	 * Selects the appropriate alphabet for the analysed text and sets the combo box
	 * selection.
	 *
	 */
	private void selectAppropriateAlphabet() {
		AbstractAlphabet[] alphas = AlphabetsManager.getInstance().getAlphabets();
		String prevAlpha = myOverlayAlphabet;

		double bestrating = -99999;
		int bestindex = 0;
		double actualrating = 0;
		for (int i = 0; i < alphas.length; i++) {
			actualrating = rateAlphabetTextDifference(String.valueOf(alphas[i].getCharacterSet()), text);
			if (actualrating > bestrating) {
				bestrating = actualrating;
				bestindex = i;
			}
		}

		String bestAlphaString = String.valueOf(alphas[bestindex].getCharacterSet());
		if (bestAlphaString != null && !bestAlphaString.equals(prevAlpha)) {
			if (combo2.isVisible() && combo2.isEnabled()) {
				tipLauncher.showNewTooltip(
						combo2.toDisplay(new Point((int) Math.round((combo2.getSize().x) * 0.612), combo2.getSize().y)),
						9000, "", Messages.FullAnalysisUI_5); //$NON-NLS-1$
			}
		}

		combo2.select(bestindex);
		combo2WidgetSelected(null);
	}

	/**
	 * analyses the selected overlay text.
	 *
	 * @param alphabet
	 *            limits the overlay analysis to a specific alphabet (each character
	 *            only to be contained once!)
	 */
	private void analyzeOverlay(final String alphabet) {
		String overlayText = reftext;
		overlayAnalysis = new FreqAnalysisCalc(overlayText, 1, 0, null, alphabet);
		if (text != null && !text.isEmpty()) {
			myLimitedAnalysis = new FreqAnalysisCalc(text, myLength, myOffset, null, myOverlayAlphabet);
			if (btnReferenceTools.getSelection())
				myGraph.setAnalysis(myLimitedAnalysis);
			// else myGraph.setAnalysis(myAnalysis);
		}
		myGraph.setOverlayAnalysis(overlayAnalysis);
		myGraph.redraw();
	}

	private void polyOnOffSelected(final SelectionEvent evt) {
		hideObject(group2, button3.getSelection());

	}

	private void combo1WidgetSelected(final SelectionEvent evt) {
		// DONT REMOVE THIS, PLEASE!
		// if (combo1.getSelectionIndex() == combo1.getItemCount()-1)
		// {
		// //no action til now..
		// }
		// //Predefined one
		// else
		// {
		reftext = reftexts.get(combo1.getSelectionIndex()).getText();
		analyzeOverlay(myOverlayAlphabet);
		// }
	}

	private void combo2WidgetSelected(final SelectionEvent evt) {
		AbstractAlphabet[] alphas = AlphabetsManager.getInstance().getAlphabets();
		myOverlayAlphabet = String.valueOf(alphas[combo2.getSelectionIndex()].getCharacterSet());
		analyzeOverlay(myOverlayAlphabet);

	}

	public final void execute(final int keyLength, final int keyPos, final int overlayIndex, final boolean resetShift,
			final boolean executeCalc, final boolean whichTab, final boolean activateOverlay) {
		if (keyLength > 0) {
			if (keyLength == 1) {
				button3.setSelection(true);
				button4.setSelection(false);
			} else {
				button3.setSelection(false);
				button4.setSelection(true);
			}
			polyOnOffSelected(null);

			spinner1.setSelection(keyLength);
			changedVigOptions();
			if (keyPos < 0) {
				text = getEditorText();
				recalcGraph();
			}
		}
		if (keyPos > -1) {
			spinner2.setSelection(keyPos);
			changedVigOptions();
			text = getEditorText();
			recalcGraph();
		}

		if (overlayIndex > -1 && overlayIndex < combo1.getVisibleItemCount() - 1) {
			combo1.select(overlayIndex);
		}

		if (resetShift) {
			myGraph.getFrequencyGraph().resetDrag();
		}
		if (executeCalc) {
			if (checkEditor()) {
				text = getEditorText();
				selectAppropriateAlphabet();
				recalcGraph();
			}

		}

		if (whichTab) {
			tabFolder1.setSelection(0);
		} else {
			tabFolder1.setSelection(1);
		}

		btnReferenceTools.setSelection(activateOverlay);
		myGraph.setOverlayActivated(btnReferenceTools.getSelection());
		myGraph.redraw();

	}

}
