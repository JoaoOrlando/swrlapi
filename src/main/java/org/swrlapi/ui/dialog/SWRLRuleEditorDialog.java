package org.swrlapi.ui.dialog;

import org.swrlapi.core.SWRLAPIOWLOntology;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.parser.SWRLIncompleteRuleException;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.parser.SWRLParser;
import org.swrlapi.parser.SWRLParserSupport;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.ui.model.SWRLAPIApplicationModel;
import org.swrlapi.ui.model.SWRLRulesTableModel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Modal dialog providing a SWRL rule and SQWRL query editor.
 *
 * @see org.swrlapi.core.SWRLAPIRule
 * @see org.swrlapi.sqwrl.SQWRLQuery
 */
public class SWRLRuleEditorDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private static final String TITLE = "Edit SWRL Rule";
	private static final String RULE_NAME_TITLE = "Rule";
	private static final String COMMENT_LABEL_TITLE = "Comment";
	private static final String STATUS_LABEL_TITLE = "Status";
	private static final String OK_BUTTON_TITLE = "Ok";
	private static final String CANCEL_BUTTON_TITLE = "Cancel";
	private static final String STATUS_OK = "Ok";
	private static final String STATUS_NO_RULE_TEXT =
			"Use Tab key to cycle through auto-completions;" + " to remove auto-complete expansion, use Escape key";
	private static final String INVALID_RULE_TITLE = "Invalid";
	private static final String MISSING_RULE = "Nothing to save!";
	private static final String MISSING_RULE_NAME_TITLE = "Empty Name";
	private static final String MISSING_RULE_NAME = "A name must be supplied!";
	private static final String QUIT_CONFIRM_TITLE = "Unsaved Changes";
	private static final String QUIT_CONFIRM_MESSAGE = "Are you sure you want discard your changes?";
	private static final String DUPLICATE_RULE_TEXT = "A rule exists with this name - please pick another name.";
	private static final String DUPLICATE_RULE_TITLE = "Duplicate Rule Name";
	private static final String INTERNAL_ERROR_TITLE = "Internal Error";

	private static final int BUTTON_PREFERRED_WIDTH = 100;
	private static final int BUTTON_PREFERRED_HEIGHT = 30;
	private static final int RULE_EDIT_AREA_COLUMNS = 20;
	private static final int RULE_EDIT_AREA_ROWS = 60;

	private final SWRLAPIApplicationModel applicationModel;
	private final SWRLAPIApplicationDialogManager applicationDialogManager;

	private final SWRLRuleEditorInitialDialogState initialDialogState = new SWRLRuleEditorInitialDialogState();

	private JButton saveButton;
	private JTextField ruleNameTextField, commentTextField, statusTextField;
	private JTextArea ruleTextTextArea;

	private boolean editMode = false;

	private final SWRLAutoCompleter autoCompleter;
	private SWRLRuleEditorAutoCompleteState autoCompleteState = null; // Non null if in auto-complete mode

	public SWRLRuleEditorDialog(SWRLAPIApplicationModel applicationModel,
			SWRLAPIApplicationDialogManager applicationDialogManager)
	{
		this.applicationModel = applicationModel;
		this.applicationDialogManager = applicationDialogManager;
		this.autoCompleter = new SWRLAutoCompleter(this.applicationModel.getSWRLAPIOWLOntology());

		setTitle(TITLE);
		setModal(true);

		createComponents();

		this.ruleTextTextArea.addKeyListener(new SWRLRuleEditorKeyAdapter());

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent we)
			{
			}
		}); // Thwart user close
	}

	@Override
	public void setVisible(boolean b)
	{
		if (b) {
			setInitialDialogState();
			disableAutoCompleteModeIfNecessary();
			updateStatus();
		}
		super.setVisible(b);
	}

	public void enableEditMode(String ruleName, String ruleText, String comment)
	{
		cancelEditMode();

		this.ruleNameTextField.setText(ruleName);
		this.ruleTextTextArea.setText(ruleText);
		this.commentTextField.setText(comment);
		this.statusTextField.setText(""); // setVisible will set appropriate initial text

		this.editMode = true;
	}

	private void cancelEditMode()
	{
		this.ruleNameTextField.setText("");
		this.ruleNameTextField.setEnabled(true);
		this.ruleTextTextArea.setText("");
		this.ruleTextTextArea.setEnabled(true);
		this.ruleTextTextArea.setText("");
		this.commentTextField.setText("");
		this.statusTextField.setText("");

		this.editMode = false;
	}

	private void createComponents()
	{
		Container contentPane = getContentPane();

		JLabel ruleNameLabel = new JLabel(RULE_NAME_TITLE);
		this.ruleNameTextField = new JTextField("");

		this.ruleTextTextArea = new JTextArea("", RULE_EDIT_AREA_COLUMNS, RULE_EDIT_AREA_ROWS);
		this.ruleTextTextArea.setBorder(BorderFactory.createLoweredBevelBorder());
		this.ruleTextTextArea.setLineWrap(true);
		this.ruleTextTextArea.setWrapStyleWord(true);

		JLabel commentLabel = new JLabel(COMMENT_LABEL_TITLE);
		this.commentTextField = new JTextField("");

		JLabel statusLabel = new JLabel(STATUS_LABEL_TITLE);
		this.statusTextField = new JTextField("");
		this.statusTextField.setEnabled(false);

		JButton cancelButton = new JButton(CANCEL_BUTTON_TITLE);
		cancelButton.setPreferredSize(new Dimension(BUTTON_PREFERRED_WIDTH, BUTTON_PREFERRED_HEIGHT));
		cancelButton.addActionListener(new CancelSWRLRuleEditActionListener(contentPane));

		saveButton = new JButton(OK_BUTTON_TITLE);
		saveButton.setPreferredSize(new Dimension(BUTTON_PREFERRED_WIDTH, BUTTON_PREFERRED_HEIGHT));
		saveButton.addActionListener(new SaveSWRLRuleActionListener(contentPane));

		contentPane.setLayout(new BorderLayout());

		JPanel upperPanel = new JPanel(new GridLayout(6, 2));
		upperPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		upperPanel.add(ruleNameLabel);
		upperPanel.add(ruleNameTextField);
		upperPanel.add(commentLabel);
		upperPanel.add(commentTextField);
		upperPanel.add(statusLabel);
		upperPanel.add(statusTextField);

		JPanel rulePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		rulePanel.add(ruleTextTextArea);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(cancelButton);
		buttonPanel.add(saveButton);

		JPanel surroundPanel = new JPanel(new BorderLayout());
		surroundPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		contentPane.add(surroundPanel, BorderLayout.CENTER);
		surroundPanel.add(upperPanel, BorderLayout.NORTH);
		surroundPanel.add(rulePanel, BorderLayout.CENTER);
		surroundPanel.add(buttonPanel, BorderLayout.SOUTH);

		pack();
	}

	private class SWRLRuleEditorKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent event)
		{
			int code = event.getKeyCode();
			if (code == KeyEvent.VK_TAB) {
				autoComplete();
				event.consume();
			} else if (code == KeyEvent.VK_ESCAPE) {
				cancelAutoCompleteIfNecessary();
				event.consume();
			} else if (code == KeyEvent.VK_DELETE) {
				cancelAutoCompleteIfNecessary();
			} else { // Any other key will disable auto-complete mode if it is active
				disableAutoCompleteModeIfNecessary();
				super.keyPressed(event);
			}
		}

		@Override
		public void keyReleased(KeyEvent event) { updateStatus(); }
	}

	private void autoComplete()
	{
		if (!isInAutoCompleteMode()) {
			String ruleText = getRuleText();
			int textPosition = this.ruleTextTextArea.getCaretPosition();
			int i = SWRLParserSupport.findSplittingPoint(ruleText.substring(0, textPosition));
			String prefix = ruleText.substring(i, textPosition);
			if (!prefix.equals("")) {
				List<String> expansions = getExpansions(prefix); // All expansions will start with the empty string.

				if (expansions.size() > 1) { // More than the empty string expansion; if not, do not enter autoComplete mode
					SWRLRuleEditorAutoCompleteState state = new SWRLRuleEditorAutoCompleteState(textPosition, prefix, expansions);
					insertExpansion(textPosition, prefix, state.getNextExpansion()); // Skip the empty string
					enableAutoCompleteMode(state);
				}
			}
		} else { // Already in auto-complete mode
			int textPosition = this.autoCompleteState.getTextPosition();
			String prefix = this.autoCompleteState.getPrefix();
			String currentExpansion = this.autoCompleteState.getCurrentExpansion();
			String nextExpansion = this.autoCompleteState.getNextExpansion();

			replaceExpansion(textPosition, prefix, currentExpansion, nextExpansion);
		}
	}

	private boolean isInAutoCompleteMode()
	{
		return this.autoCompleteState != null;
	}

	private void enableAutoCompleteMode(SWRLRuleEditorAutoCompleteState autoCompleteState)
	{
		this.autoCompleteState = autoCompleteState;
	}

	private void disableAutoCompleteModeIfNecessary()
	{
		if (this.autoCompleteState != null)
			disableAutoCompleteMode();
	}

	private void disableAutoCompleteMode()
	{
		this.autoCompleteState = null;
	}

	private void cancelAutoCompleteIfNecessary()
	{
		if (isInAutoCompleteMode()) {
			int textPosition = this.autoCompleteState.getTextPosition();
			String prefix = this.autoCompleteState.getPrefix();
			String currentExpansion = this.autoCompleteState.getCurrentExpansion();

			replaceExpansion(textPosition, prefix, currentExpansion, "");
			disableAutoCompleteMode();
		}
	}

	private void insertExpansion(int textPosition, String prefix, String expansion)
	{
		String expansionTail = expansion.substring(prefix.length());

		try {
			this.ruleTextTextArea.getDocument().insertString(textPosition, expansionTail, null);
		} catch (BadLocationException e) {
			disableAutoCompleteMode();
		}
	}

	private void replaceExpansion(int textPosition, String prefix, String currentExpansion, String nextExpansion)
	{
		String currentExpansionTail = currentExpansion.isEmpty() ? "" : currentExpansion.substring(prefix.length());
		String nextExpansionTail = nextExpansion.isEmpty() ? "" : nextExpansion.substring(prefix.length());

		try {
			if (!currentExpansionTail.isEmpty())
				this.ruleTextTextArea.getDocument().remove(textPosition, currentExpansionTail.length());

			if (!nextExpansionTail.isEmpty())
				this.ruleTextTextArea.getDocument().insertString(textPosition, nextExpansionTail, null);
		} catch (BadLocationException e) {
			disableAutoCompleteMode();
		}
	}

	private List<String> getExpansions(String prefix)
	{
		List<String> expansions = new ArrayList<>();

		expansions.add(""); // Add empty expansion that we can cycle back to
		expansions.addAll(this.autoCompleter.getCompletions(prefix));

		return expansions;
	}

	private class CancelSWRLRuleEditActionListener implements ActionListener
	{
		private final Component parent;

		public CancelSWRLRuleEditActionListener(Component parent)
		{
			this.parent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			boolean okToQuit;

			if (hasDialogStateChanged()) {
				okToQuit = getApplicationDialogManager().showConfirmDialog(parent, QUIT_CONFIRM_MESSAGE, QUIT_CONFIRM_TITLE);
			} else
				okToQuit = true;

			if (okToQuit) {
				cancelEditMode();
				setVisible(false);
			}
		}
	}

	private class SaveSWRLRuleActionListener implements ActionListener
	{
		private final Component parent;

		public SaveSWRLRuleActionListener(Component parent)
		{
			this.parent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String ruleName = getRuleName();
			String ruleText = getRuleText();
			String comment = getComment();
			boolean errorOccurred;

			if (ruleName.trim().equals("")) {
				getApplicationDialogManager().showErrorMessageDialog(parent, MISSING_RULE_NAME, MISSING_RULE_NAME_TITLE);
				errorOccurred = true;
			} else if (ruleText.trim().equals("")) {
				getApplicationDialogManager().showErrorMessageDialog(parent, MISSING_RULE, MISSING_RULE);
				errorOccurred = true;
			} else if (getSWRLRulesTableModel().hasSWRLRule(ruleName) && !editMode) {
				getApplicationDialogManager().showErrorMessageDialog(parent, DUPLICATE_RULE_TEXT, DUPLICATE_RULE_TITLE);
				errorOccurred = true;
			} else {
				try {
					if (editMode) {
						deleteSWRLRule(initialDialogState.getRuleName());
						createSWRLRule(ruleName, ruleText, comment, true);
						errorOccurred = false;
					} else {
						if (getSWRLRulesTableModel().hasSWRLRule(ruleName)) {
							getApplicationDialogManager().showErrorMessageDialog(parent, DUPLICATE_RULE_TEXT, DUPLICATE_RULE_TITLE);
							errorOccurred = true;
						} else {
							createSWRLRule(ruleName, ruleText, comment, true);
							errorOccurred = false;
						}
					}
				} catch (SWRLParseException pe) {
					getApplicationDialogManager().showErrorMessageDialog(parent, pe.getMessage(), INVALID_RULE_TITLE);
					errorOccurred = true;
				} catch (SQWRLException pe) {
					getApplicationDialogManager().showErrorMessageDialog(parent, pe.getMessage(), INVALID_RULE_TITLE);
					errorOccurred = true;
				} catch (RuntimeException pe) {
					getApplicationDialogManager().showErrorMessageDialog(parent, pe.getMessage(), INTERNAL_ERROR_TITLE);
					errorOccurred = true;
				}
			}

			if (!errorOccurred) {
				setVisible(false);
				cancelEditMode();
			} else
				updateStatus();
		}
	}

	private void updateStatus()
	{
		String ruleText = getRuleText();

		if (ruleText.isEmpty()) {
			setStatusText(STATUS_NO_RULE_TEXT);
			disableSave();
		} else {
			try {
				getSWRLParser().parseSWRLRule(ruleText, true, getRuleName(), getComment());
				setStatusText(STATUS_OK);
				enableSave();
			} catch (SWRLIncompleteRuleException e) {
				setStatusText(e.getMessage());
				disableSave();
			} catch (SWRLParseException e) {
				setStatusText("Parse error: " + e.getMessage());
				disableSave();
			} catch (RuntimeException e) {
				setStatusText("Error: " + e.getMessage());
				disableSave();
			}
		}
	}

	private void disableSave()
	{
		this.saveButton.setEnabled(false);
	}

	private void enableSave()
	{
		this.saveButton.setEnabled(true);
	}

	private void setStatusText(String status)
	{
		this.statusTextField.setText(status);
	}

	private String getRuleName()
	{
		return this.ruleNameTextField.getText().trim();
	}

	private String getComment()
	{
		return this.commentTextField.getText().trim();
	}

	private String getRuleText()
	{ // We replace the Unicode characters when parsing
		return this.ruleTextTextArea.getText().trim().replaceAll(Character.toString(SWRLParser.AND_CHAR), "^")
				.replaceAll(Character.toString(SWRLParser.IMP_CHAR), "->")
				.replaceAll(Character.toString(SWRLParser.RING_CHAR), ".");
	}

	private void createSWRLRule(String ruleName, String rule, String comment, boolean isActive)
			throws SWRLParseException, SQWRLException
	{
		SWRLAPIRule swrlapiRule = getSWRLAPIOWLOntology().createSWRLRule(ruleName, rule, comment, isActive);

		if (swrlapiRule.isSQWRLQuery())
			getSWRLAPIOWLOntology().createSQWRLQuery(ruleName, rule, comment, isActive);

		getSWRLRulesTableModel().addSWRLRule(swrlapiRule);
	}

	private void deleteSWRLRule(String ruleName)
	{
		getSWRLRulesTableModel().removeSWRLRule(ruleName);
		this.applicationModel.getSWRLAPIOWLOntology().deleteSWRLRule(ruleName);
	}

	private SWRLParser getSWRLParser()
	{
		return this.applicationModel.getSWRLParser();
	}

	private SWRLRulesTableModel getSWRLRulesTableModel()
	{
		return this.applicationModel.getSWRLRulesTableModel();
	}

	private SWRLAPIApplicationDialogManager getApplicationDialogManager()
	{
		return this.applicationDialogManager;
	}

	private SWRLAPIOWLOntology getSWRLAPIOWLOntology() { return this.applicationModel.getSWRLAPIOWLOntology(); }

	private void setInitialDialogState()
	{
		this.initialDialogState.setState(getRuleName(), getComment(), getRuleText());
	}

	private boolean hasDialogStateChanged()
	{
		return this.initialDialogState.hasStateChanged(getRuleName(), getComment(), getRuleText());
	}
}
