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
    JComboBox<String> comboBox;
    JPanel panelTasks;
    JScrollPane scrollPane;
    JTextField textField;
    String placeholder;
    String[] catArr = new String[]{"Daily","Weekly","Monthly","Yearly"};
    File taskFile;
    File completedTaskFile;
    List<String> taskList;
    List<String> completedTaskList;


    MyFrame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setSize(700,638);
        this.setResizable(false);
        placeholder="Enter task name";

        panelHeader = new JPanel();
        labelTitle = new JLabel("ToDo List");
        buttonAdd = new JButton("Add Task");
        comboBox = new JComboBox<>(catArr);
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
        labelTitle.setBounds(250,0,250,80);
        labelTitle.setFont(new Font("Segoe UI",Font.BOLD,45));
        labelTitle.setHorizontalAlignment(JLabel.LEFT);

        buttonAdd.setFocusPainted(false);
        buttonAdd.setBounds(500,80,150,50);
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

        comboBox.setBounds(50,80,150,50);
        comboBox.setFont(new Font("Segoe UI",Font.PLAIN,25));
        comboBox.addActionListener((e)->  {
                if (e.getSource()==comboBox){
                    loadTasksOnPanel();
                }
        });

        textField.setText(placeholder);
        textField.setForeground(Color.gray);
        textField.setBounds(250,85,200,40);
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


        panelTasks.setPreferredSize(new Dimension(700,taskList.size()*49+6));
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
            panelTasks.setPreferredSize(new Dimension(700,taskList.size()*49+6));

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
        //initialization
        JPanel innerPanel = new JPanel();
        JLabel innerLabel = new JLabel(task);
        JButton deleteButton = new JButton();
        JButton checkButton = new JButton();
        JButton editButton = new JButton();
        JButton sendButton = new JButton();
        JComboBox<String> sendComboBox = new JComboBox<>(catArr);
        ImageIcon del = new ImageIcon("icons/delete32.png");
        ImageIcon delHov = new ImageIcon("icons/deleteHover32.png");
        ImageIcon checkBoxBlank = new ImageIcon("icons/unchecked.png");
        ImageIcon checkBoxHover = new ImageIcon("icons/checkbox.png");
        ImageIcon checkBoxFilled = new ImageIcon("icons/square.png");
        ImageIcon edit = new ImageIcon("icons/pencil.png");
        ImageIcon editHover = new ImageIcon("icons/pencilHover.png");
        ImageIcon send = new ImageIcon("icons/reply.png");
        ImageIcon sendHover = new ImageIcon("icons/replyHover.png");
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

        //Task name label
        innerLabel.setFont(new Font("Segoe UI",Font.PLAIN,25));
        innerLabel.setToolTipText(innerLabel.getText());
        innerLabel.setBounds(innerPanel.getX()+50,innerPanel.getY(),470,40);
        innerLabel.setForeground(Color.black);

        //Button for checking a task
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
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkButton.removeMouseListener(mouseAdapter);
                checkButton.setIcon(checkBoxFilled);
                completedTaskList.add(innerLabel.getText());
                updateTxtFile(completedTaskList,completedTaskFile);
                innerLabel.setForeground(Color.gray);
                innerLabel.setText("<html><s>" + innerLabel.getText()+ "<s></html");
                innerPanel.remove(editButton);
                innerPanel.revalidate();
                innerPanel.repaint();
            }
        });
        //Button for editing a task
        editButton.setBounds(innerPanel.getX()+560,innerPanel.getY()+4,32,32);
        editButton.setIcon(edit);
        editButton.setFocusPainted(false);
        editButton.setBackground(null);
        editButton.setBorder(null);
        editButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                editButton.setIcon(editHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                editButton.setIcon(edit);
            }
        });
        editButton.addActionListener((e)->{
            editTask(innerLabel,innerPanel);
        });


        //Button for sending a task to another category
        sendButton.setBounds(innerPanel.getX()+520,innerPanel.getY()+4,32,32);
        sendButton.setIcon(send);
        sendButton.setFocusPainted(false);
        sendButton.setBackground(null);
        sendButton.setBorder(null);
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setIcon(sendHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setIcon(send);
            }
        });
        //Display combobox options when edit button is clicked
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                JPopupMenu menu = new JPopupMenu();
                for (int i = 0;i<catArr.length;i++) {
                    if (i == comboBox.getSelectedIndex()) continue;
                    else{
                        int j = i;
                        JMenuItem item = new JMenuItem(catArr[i]);
                        menu.add(item);
                        item.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                removeTask(task,innerPanel,j);
                            }
                        });
                    }
                }
                if (e.getButton()==1){
                    menu.show(e.getComponent(),e.getX(),e.getY());
                }
            }
        });

        //Menu comboBox to pick to which category should the task go
        sendComboBox.setVisible(false);
        sendComboBox.setBounds(sendButton.getX()+32,sendButton.getY()+32,150,30);

        //Button for deleting a task
        deleteButton.setBounds(innerPanel.getX()+600,innerPanel.getY()+4,32,32);
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
            removeTask(task,innerPanel);
        });


        //adding all elements to the inner panel
        innerPanel.add(checkButton);
        innerPanel.add(innerLabel);
        innerPanel.add(sendButton);
        innerPanel.add(sendComboBox);
        if (!completedTaskList.contains(task)) innerPanel.add(editButton);
        innerPanel.add(deleteButton);
        innerPanel.setLayout(null);
        innerPanel.setBackground(Color.lightGray);
        innerPanel.setPreferredSize(new Dimension(650,40));

        //adding the inner panel to the larger one
        panelTasks.add(innerPanel);
        panelTasks.revalidate();
        panelTasks.repaint();
    }

    //Remove a task from the task panel and update txt file and list
    private void removeTask(String task, JPanel innerPanel){
        taskList.remove(comboBox.getSelectedIndex()+task);
        panelTasks.setPreferredSize(new Dimension(700,taskList.size()*49+6));
        updateTxtFile(taskList,taskFile);
        panelTasks.remove(innerPanel);
        panelTasks.revalidate();
        panelTasks.repaint();
    }

    //Remove a task from the task panel, update txt file and list also send this task to another category
    private void removeTask(String task, JPanel innerPanel, int i){
        taskList.remove(comboBox.getSelectedIndex()+task);
        taskList.add(i+task);
        panelTasks.setPreferredSize(new Dimension(700,taskList.size()*49+6));
        updateTxtFile(taskList,taskFile);
        panelTasks.remove(innerPanel);
        panelTasks.revalidate();
        panelTasks.repaint();
    }

    private void editTask(JLabel label, JPanel innerPanel){
        String task = label.getText();
        JTextField editField = new JTextField();

        editField.setText(task);
        editField.setBounds(label.getBounds());
        editField.setFont(label.getFont());
        editField.setForeground(label.getForeground());
        editField.setBackground(label.getBackground());
        editField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    if (!editField.getText().isEmpty()&&!editField.getText().equals(placeholder)){
                        innerPanel.remove(editField);
                        label.setText(editField.getText());
                        label.setToolTipText(label.getText());
                        innerPanel.add(label);
                        taskList.add(taskList.indexOf(comboBox.getSelectedIndex()+task),
                                comboBox.getSelectedIndex()+editField.getText());
                        taskList.remove(comboBox.getSelectedIndex()+task);
                        updateTxtFile(taskList,taskFile);
                        innerPanel.revalidate();
                        innerPanel.repaint();
                        loadTasksOnPanel();
                    }
                }
            }
        });


        innerPanel.remove(label);
        innerPanel.add(editField);
        innerPanel.revalidate();
        innerPanel.repaint();
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
