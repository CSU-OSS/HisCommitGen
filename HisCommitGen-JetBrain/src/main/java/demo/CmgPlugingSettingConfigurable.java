package demo;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class CmgPlugingSettingConfigurable implements Configurable {
    private JPanel myMainPanel;
    private JCheckBox historyCheckBox;
    private JCheckBox needRecCheckBox;
    private JTextField temperatureTextField;
    private JSlider temperatureSlider;
    private JTextField maxTokenTextField;

    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "CMG Plunging Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        myMainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        historyCheckBox = new JCheckBox("Use History Commit Messages", MyPluginSettings.getInstance().getState().useHistoryOrNot );
        needRecCheckBox = new JCheckBox("Generate More Recommended Messages",MyPluginSettings.getInstance().getState().needRec);

        // 创建滑动条
        int temperatureValue = (int)Double.parseDouble(MyPluginSettings.getInstance().getState().temperature)*10;
        temperatureSlider = new JSlider(0, 20, temperatureValue);
        temperatureSlider.addChangeListener(e -> {
            double value = temperatureSlider.getValue() / 10.0;
            temperatureTextField.setText(String.valueOf(value));
        });

        Font font = historyCheckBox.getFont();
        JLabel temperatureLabel = new JLabel("Model Temperature");
        temperatureLabel.setFont(font);
        JLabel maxTokenLabel = new JLabel("Max Tokens");
        maxTokenLabel.setFont(font);


        temperatureTextField = new JTextField(String.valueOf(MyPluginSettings.getInstance().getState().temperature),10);
        temperatureTextField.setEnabled(false);
        maxTokenTextField = new JTextField(String.valueOf(MyPluginSettings.getInstance().getState().maxToken),10);


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH; // Align component to the top
        gbc.gridwidth = 2;
        myMainPanel.add(historyCheckBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTH; // Align component to the top
        gbc.gridwidth = 2;
        myMainPanel.add(needRecCheckBox,gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTH; // Align component to the top
        gbc.gridwidth = 2;
        myMainPanel.add(temperatureLabel,gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        myMainPanel.add(temperatureSlider,gbc);

        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        myMainPanel.add(temperatureTextField,gbc);


        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        myMainPanel.add(maxTokenLabel,gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        myMainPanel.add(maxTokenTextField,gbc);

        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        myMainPanel.add(new Label("word"),gbc);


        return myMainPanel;
    }

    @Override
    public boolean isModified() {
        boolean modified = historyCheckBox.isSelected() != MyPluginSettings.getInstance().getState().useHistoryOrNot;
        modified |= needRecCheckBox.isSelected() != MyPluginSettings.getInstance().getState().needRec;
//        modified |= temperatureTextField.getText() != MyPluginSettings.getInstance().getState().temperature;

        return modified;
    }

    @Override
    public void apply() throws ConfigurationException {
        Objects.requireNonNull(MyPluginSettings.getInstance().getState()).useHistoryOrNot = historyCheckBox.isSelected();
        MyPluginSettings.getInstance().getState().needRec = needRecCheckBox.isSelected();
        MyPluginSettings.getInstance().getState().temperature = temperatureTextField.getText();
        MyPluginSettings.getInstance().getState().maxToken = maxTokenTextField.getText();
    }

    @Override
    public void reset() {
        historyCheckBox.setSelected(MyPluginSettings.getInstance().getState().useHistoryOrNot);
        needRecCheckBox.setSelected(MyPluginSettings.getInstance().getState().needRec);
        temperatureTextField.setText(String.valueOf(MyPluginSettings.getInstance().getState().temperature));
        maxTokenTextField.setText(String.valueOf(MyPluginSettings.getInstance().getState().maxToken));
    }

    @Override
    public void disposeUIResources() {
        myMainPanel = null;
        historyCheckBox = null;
        needRecCheckBox = null;
        temperatureTextField = null;
        maxTokenTextField = null;
    }
}
