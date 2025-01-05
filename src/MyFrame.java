import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class MyFrame extends JFrame implements ActionListener{

    JLabel labelTitle;
    JPanel panelHeader;
    JButton buttonAdd;
    JComboBox comboBox;
    JPanel panelTasks;
    JScrollPane scrollPane;
    JTextField textField;
    String placeholder;
    File taskFile;
    File completedTaskFile;
    List<String> taskList;
    List<String> completedTaskList;


    MyFrame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setSize(600,638);
        this.setResizable(false);
        placeholder="Enter task name";

        panelHeader = new JPanel();
        labelTitle = new JLabel("ToDo List");
        buttonAdd = new JButton("Add Task");
        comboBox = new JComboBox(new String[]{"Daily","Weekly","Monthly","Yearly"});
        panelTasks = new JPanel();
        textField = new JTextField(20);
        taskFile = new File("list_of_tasks.txt");
        completedTaskFile = new File("list_of_completed_tasks.txt");
        taskList = new ArrayList<>();
        completedTaskList = new ArrayList<>();
        scrollPane = new JScrollPane(panelTasks,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        fillListWithTxt(taskList,taskFile);
        fillListWithTxt(completedTaskList,completedTaskFile);
        this.loadTasksOnPanel();

        panelHeader.setPreferredSize(new Dimension(600,150));
        panelHeader.setBackground(new Color(255,100,100));
        panelHeader.setLayout(null);
        panelHeader.add(labelTitle);
        panelHeader.add(buttonAdd);
        panelHeader.add(textField);
        panelHeader.add(comboBox);

        labelTitle.setOpaque(true);
        labelTitle.setBackground(panelHeader.getBackground());
        labelTitle.setBounds(190,0,250,80);
        labelTitle.setFont(new Font("Segoe UI",Font.BOLD,45));
        labelTitle.setHorizontalAlignment(JLabel.LEFT);

        buttonAdd.setFocusPainted(false);
        buttonAdd.setBounds(410,80,150,50);
        buttonAdd.setBackground(new Color(255,255,255));
        buttonAdd.setFont(new Font("Segoe UI",Font.PLAIN,25));
        buttonAdd.addActionListener(this);
        buttonAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonAdd.setBackground(new Color(150,150,150));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buttonAdd.setBackground(new Color(255,255,255));
            }
        });

        comboBox.setBounds(20,80,150,50);
        comboBox.setFont(new Font("Segoe UI",Font.PLAIN,25));
        comboBox.addActionListener((e)->  {
                if (e.getSource()==comboBox){
                    loadTasksOnPanel();
                }
        });

        textField.setText(placeholder);
        textField.setForeground(Color.gray);
        textField.setBounds(190,85,200,40);
        textField.setFont(new Font("Segoe UI",Font.PLAIN,20));
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER) addTaskFunc();
            }
        });


        panelTasks.setPreferredSize(new Dimension(600,taskList.size()*49+6));
        panelTasks.setLayout(new FlowLayout(FlowLayout.LEFT,10,9));

        scrollPane.getVerticalScrollBar().setUnitIncrement(10);


        this.add(scrollPane);
        this.add(panelHeader,BorderLayout.NORTH);
        this.requestFocusInWindow();
        this.setTitle("ToDo List");
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        SwingUtilities.invokeLater(() ->buttonAdd.requestFocusInWindow());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==buttonAdd){
            addTaskFunc();
        }
    }

    //Function to add a task (When enter is pressed or when button is clicked)
    private void addTaskFunc(){
        if (textField.getText().isEmpty()||textField.getText().equals(placeholder)){
            JOptionPane.showMessageDialog(null,
                    "Please enter a valid task name", "Blank task",
                    JOptionPane.WARNING_MESSAGE);
            textField.setForeground(Color.gray);
            textField.setText(placeholder);
        } /*else if (taskList.size()>9){
           JOptionPane.showMessageDialog(null,
                   "Complete a task before adding a new one!", "Too many tasks",
                    JOptionPane.WARNING_MESSAGE);
            textField.setForeground(Color.gray);
            textField.setText(placeholder);
        }*/ else if(taskList.contains(textField.getText())){
            JOptionPane.showMessageDialog(null,
                    "This task already exists", "Duplicate Task",
                    JOptionPane.WARNING_MESSAGE);
            textField.setForeground(Color.gray);
            textField.setText(placeholder);
        }
        else{
            String taskName = textField.getText();
            textField.setText("");
            taskList.add(comboBox.getSelectedIndex()+taskName);
            panelTasks.setPreferredSize(new Dimension(600,taskList.size()*49+6));

            this.updateTxtFile(taskList,taskFile);
            this.loadSingleTask(taskName);

            textField.setText(placeholder);
            textField.setForeground(Color.GRAY);
            SwingUtilities.invokeLater(() ->buttonAdd.requestFocusInWindow());
        }
    }

    //Load all tasks on panelTasks
    private void loadTasksOnPanel(){
        panelTasks.removeAll();
        panelTasks.repaint();
        for (String task : taskList){
            if (task.charAt(0)==comboBox.getSelectedIndex()+'0'){
                loadSingleTask(task.substring(1));
            }
        }

    }

    //load a single task on panelTasks
    private void loadSingleTask(String task){
        JPanel innerPanel = new JPanel();
        JLabel innerLabel = new JLabel(task);
        JButton deleteButton = new JButton();
        JButton checkButton = new JButton();
        ImageIcon del = new ImageIcon("delete32.png");
        ImageIcon delHov = new ImageIcon("deleteHover32.png");
        ImageIcon checkBoxBlank = new ImageIcon("unchecked.png");
        ImageIcon checkBoxHover = new ImageIcon("checkbox.png");
        ImageIcon checkBoxFilled = new ImageIcon("square.png");
        MouseAdapter mouseAdapter = new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                checkButton.setIcon(checkBoxFilled);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                checkButton.setIcon(checkBoxHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                checkButton.setIcon(checkBoxBlank);
            }
        };

        innerLabel.setFont(new Font("Segoe UI",Font.PLAIN,25));
        innerLabel.setToolTipText(innerLabel.getText());
        innerLabel.setBounds(innerPanel.getX()+50,innerPanel.getY(),400,40);
        innerLabel.setForeground(Color.black);

        checkButton.setBounds(innerPanel.getX()+6,innerPanel.getY()+4,32,32);
        if (completedTaskList.contains(task)){
            checkButton.setIcon(checkBoxFilled);
            innerLabel.setForeground(Color.gray);
            innerLabel.setText("<html><s>" + innerLabel.getText()+ "<s></html");
        } else{
            checkButton.setIcon(checkBoxBlank);
            checkButton.addMouseListener(mouseAdapter);
        }
        checkButton.setFocusable(false);
        checkButton.setBackground(null);
        checkButton.setBorder(null);
        checkButton.addActionListener((e)-> {
                checkButton.removeMouseListener(mouseAdapter);
                checkButton.setIcon(checkBoxFilled);
                completedTaskList.add(task);
                updateTxtFile(completedTaskList,completedTaskFile);
                innerLabel.setForeground(Color.gray);
                innerLabel.setText("<html><s>" + innerLabel.getText()+ "<s></html");
        });

        deleteButton.setBounds(innerPanel.getX()+500,innerPanel.getY()+4,32,32);
        deleteButton.setIcon(del);
        deleteButton.setFocusable(false);
        deleteButton.setBackground(null);
        deleteButton.setBorder(null);
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                deleteButton.setIcon(delHov);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                deleteButton.setIcon(del);
            }
        });
        deleteButton.addActionListener((e)->  {
                taskList.remove(comboBox.getSelectedIndex()+task);
                panelTasks.setPreferredSize(new Dimension(600,taskList.size()*49+6));
                updateTxtFile(taskList,taskFile);
                panelTasks.remove(innerPanel);
                panelTasks.revalidate();
                panelTasks.repaint();
        });

        innerPanel.add(checkButton);
        innerPanel.add(innerLabel);
        innerPanel.add(deleteButton);
        innerPanel.setLayout(null);
        innerPanel.setBackground(Color.lightGray);
        innerPanel.setPreferredSize(new Dimension(550,40));

        panelTasks.add(innerPanel);
        panelTasks.revalidate();
        panelTasks.repaint();
    }

    //fills a List with every line of a txt file
    private void fillListWithTxt(List<String> list, File txtFile){
        if (txtFile.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
                String line;
                while((line=reader.readLine())!=null){
                    list.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else{
            try {
                txtFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }


    //Writes every element from a list in a txt file
    private void updateTxtFile(List<String> list, File file){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String task : list){
                writer.write(task);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
