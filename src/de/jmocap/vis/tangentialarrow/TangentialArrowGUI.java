package de.jmocap.vis.tangentialarrow;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * @author Franziska Zamponi
 * @date 29.06.13
 */
public class TangentialArrowGUI {

    private TangentialArrowController _tac;
    private JFrame _frame;
    private JPanel _panelInsert;
    private JLabel _labelStartSec;
    private JTextField _textStartSec;
    private JLabel _labelEndSec;
    private JTextField _textEndSec;
    private JLabel _labelFigure;
    private JTextField _textFigure;
    private JLabel _labelJoint;
    private JTextField _textJoint;
    private JPanel _panelButtons;
    private JButton _buttonAdd, _buttonRemove;
    private JPanel _panelMessage;       // for error messages
    private JTextField _textMessage;    // "
    private JPanel _panelList;
    private JList _list;
    private JScrollPane _scrollList;

    public TangentialArrowGUI(TangentialArrowController tac) {
        _tac = tac;

        _frame = new JFrame();

        _panelInsert = new JPanel(new FlowLayout());
        _labelStartSec = new JLabel(" start time: ");
        _textStartSec = new JTextField(10);
        _panelInsert.add(_labelStartSec);
        _panelInsert.add(_textStartSec);
        _labelEndSec = new JLabel(" end time: ");
        _textEndSec = new JTextField(10);
        _panelInsert.add(_labelEndSec);
        _panelInsert.add(_textEndSec);
        _labelFigure = new JLabel(" figure: ");
        _textFigure = new JTextField(10);
        _panelInsert.add(_labelFigure);
        _panelInsert.add(_textFigure);
        _labelJoint = new JLabel(" joint: ");
        _textJoint = new JTextField(10);
        _panelInsert.add(_labelJoint);
        _panelInsert.add(_textJoint);
        _frame.getContentPane().add(BorderLayout.WEST, _panelInsert);

        _panelButtons = new JPanel();
        _buttonAdd = new JButton("add");
        _panelButtons.add(_buttonAdd);
        _buttonRemove = new JButton("remove");
        _panelButtons.add(_buttonRemove);
        _frame.getContentPane().add(BorderLayout.SOUTH, _panelButtons);
        _buttonAdd.addActionListener(new ButtonAddListener());
        _buttonRemove.addActionListener(new ButtonRemoveListener());

//        _list = new JList(_tac.getTaArMaps().toArray());
        _list = new JList(new DefaultListModel());
        updateList(_list, _tac.getTaArMaps());
        _scrollList = new JScrollPane(_list);
        _panelList = new JPanel();
        _panelList.add(_scrollList);
        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _frame.getContentPane().add(BorderLayout.EAST, _panelList);

        _frame.setSize(500, 500);
        _frame.setVisible(true);
    }

    public void updateList(JList list, List<TangentialArrowController.TaArMap> objs) {
        DefaultListModel listModel = (DefaultListModel) list.getModel();
        listModel.clear();
        listModel.removeAllElements();
        for (int i = 0; i < objs.size(); i++) {
            listModel.addElement(objs.get(i).toString());
        }
    }

    class ButtonAddListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            double start = Double.parseDouble(_textStartSec.getText());
            double end = Double.parseDouble(_textEndSec.getText());
            _tac.addTaArMap(start, end, _textFigure.getText(), _textJoint.getText());

            // make changes visible:
            updateList(_list, _tac.getTaArMaps());
        }
    }

    class ButtonRemoveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            _tac.removeTaArMap(_list.getSelectedValue().toString());

            // make changes visible:
            updateList(_list, _tac.getTaArMaps());
        }
    }
}
