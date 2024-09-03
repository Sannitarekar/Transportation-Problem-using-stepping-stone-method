import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import static java.lang.Math.abs;
class Path
{
    int r,c;
    public Path(int i,int j)
    {
        this.r=i;
        this.c=j;
    }
}


public class TransportProblemGUI extends JFrame implements ActionListener {
    private JPanel panel1, panel2, panel3;
    private static JLabel totalCostLabel;
    private JLabel totalCostLabel1;
    public static int rows, columns;
    private JTextField[][] costFields;
    private JTextField[] supplyFields;
    private JTextField[] demandFields;
    private JButton generateButton, nextButton, calculateButton;
    public static int[][] cost, allotment;
    private int[] supply;
    private int[] demand;
    private int[] rowPenalty;
    private int[] columnPenalty;
    public static Path[][] path = new Path[10][10];
    public static int totalCost;
    public int totalCost1;
    public int vam;


    public TransportProblemGUI() {
        setTitle("STEPPING STONE CALCULATOR");
        ImageIcon logo = new ImageIcon("tr.png");
        setIconImage(logo.getImage());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        JLabel rowLabel = new JLabel("Number of Sources: ");
        rowLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JTextField rowField = new JTextField(5);
        rowField.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel columnLabel = new JLabel("Number of Destinations: ");
        columnLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JTextField columnField = new JTextField(5);
        columnField.setFont(new Font("Arial", Font.BOLD, 20));
        generateButton = createHoverButton("Generate");
        panel1.add(rowLabel);
        panel1.add(rowField);
        panel1.add(columnLabel);
        panel1.add(columnField);
        panel1.add(generateButton);
        add(panel1, BorderLayout.NORTH);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    rows = Integer.parseInt(rowField.getText());
                    columns = Integer.parseInt(columnField.getText());
                    initializePanel2();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(TransportProblemGUI.this,
                            "Please enter valid integers for rows and columns.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pack();
        setVisible(true);
    }

    private JButton createHoverButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIManager.getColor("Button.background"));
            }
        });
        return button;
    }

    public void initializePanel2() {
        remove(panel1);
    
        panel2 = new JPanel(new BorderLayout());
    
        JLabel headingLabel = new JLabel("Cost Matrix");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 40));
        headingLabel.setHorizontalAlignment(JLabel.CENTER);
        panel2.add(headingLabel, BorderLayout.NORTH);
    
        JPanel matrixPanel = new JPanel(new GridLayout(rows, columns));
        costFields = new JTextField[rows][columns];
        allotment = new int[rows][columns]; // Initialize the allotment array
    
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                costFields[i][j] = new JTextField(5);
                Font font = new Font("Arial", Font.BOLD, 40); // Adjust the font size as needed
                costFields[i][j].setFont(font);
                costFields[i][j].setHorizontalAlignment(JTextField.CENTER);
                costFields[i][j].setText("Source " + (i + 1) + ", Destination " + (j + 1));
                costFields[i][j].addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        JTextField textField = (JTextField) e.getSource();
                        textField.setText("");
                    }
                });
                matrixPanel.add(costFields[i][j]);
            }
        }
    
        panel2.add(matrixPanel, BorderLayout.CENTER);
    
        nextButton = createHoverButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 20));
        nextButton.setPreferredSize(new Dimension(120, 80));
        panel2.add(nextButton, BorderLayout.SOUTH);
    
        nextButton.addActionListener(this);
    
        panel2.add(nextButton, BorderLayout.SOUTH);

        add(panel2, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            cost = new int[rows][columns];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    try {
                        cost[i][j] = Integer.parseInt(costFields[i][j].getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter valid integers in all cost fields.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            initializePanel3();
        } else if (e.getSource() == calculateButton) {
            displayResults();
        }
    }

    public void initializePanel3() {
        remove(panel2);
        panel3 = new JPanel(new BorderLayout());
    
        JLabel headingLabel = new JLabel("Cost Matrix");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 40));
        headingLabel.setHorizontalAlignment(JLabel.CENTER);
    
        // Create a JTable to display the cost matrix
        String[] columnHeaders = new String[columns + 1]; // Add one for the source column
        columnHeaders[0] = "Source"; // Label for the source column
        for (int i = 0; i < columns; i++) {
            columnHeaders[i + 1] = "Destination " + (i + 1);
        }
    
        Object[][] rowData = new Object[rows][columns + 1]; // Add one for the source column
        for (int i = 0; i < rows; i++) {
            rowData[i][0] = "Source " + (i + 1); // Populate the source column
            for (int j = 0; j < columns; j++) {
                rowData[i][j + 1] = cost[i][j];
            }
        }
    
        JTable costTable = new JTable(rowData, columnHeaders);
        costTable.setFont(new Font("Arial", Font.BOLD, 20));
    
        // Create a custom cell renderer to center-align and increase the size of cost fields
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        costTable.setDefaultRenderer(Object.class, centerRenderer);
    
        costTable.setRowHeight(60);
        costTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Adjust the width for the source column
    
        JTableHeader header = costTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setBackground(Color.LIGHT_GRAY);
        header.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add a border to the header
    
        JScrollPane tableScrollPane = new JScrollPane(costTable);
    
        // Create a panel for supply and demand input
        JPanel supplyDemandPanel = new JPanel(new BorderLayout());
    
        JPanel supplyPanel = new JPanel(new FlowLayout());
        JLabel supplyLabel = new JLabel("Supply: ");
        supplyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        supplyPanel.add(supplyLabel);
        supplyFields = new JTextField[rows];
        for (int i = 0; i < rows; i++) {
            supplyFields[i] = new JTextField(5);
            supplyFields[i].setFont(new Font("Arial", Font.BOLD, 20));
            supplyFields[i].setHorizontalAlignment(JTextField.CENTER);
            supplyPanel.add(supplyFields[i]);
        }
        supplyDemandPanel.add(supplyPanel, BorderLayout.NORTH);
    
        JPanel demandPanel = new JPanel(new FlowLayout());
        JLabel demandLabel = new JLabel("Demand: ");
        demandLabel.setFont(new Font("Arial", Font.BOLD, 20));
        demandPanel.add(demandLabel);
        demandFields = new JTextField[columns];
        for (int i = 0; i < columns; i++) {
            demandFields[i] = new JTextField(5);
            demandFields[i].setFont(new Font("Arial", Font.BOLD, 20));
            demandFields[i].setHorizontalAlignment(JTextField.CENTER);
            demandPanel.add(demandFields[i]);
        }
        supplyDemandPanel.add(demandPanel, BorderLayout.CENTER);
    
        JPanel buttonPanel = new JPanel(new FlowLayout());
        calculateButton = createHoverButton("Calculate");
        calculateButton.setFont(new Font("Arial", Font.BOLD, 20));
        calculateButton.addActionListener(this);
        buttonPanel.add(calculateButton);

        JPanel totalCostPanel1 = new JPanel(new FlowLayout());
        totalCostLabel1 = new JLabel("Cost After applying VAM: ");
        totalCostLabel1.setFont(new Font("Arial", Font.BOLD, 40));
        totalCostPanel1.add(totalCostLabel1);
    
        JPanel totalCostPanel = new JPanel(new FlowLayout());
        totalCostLabel = new JLabel("Cost after applying Stepping stone method: ");
        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 40));
        totalCostPanel.add(totalCostLabel);
    
        JPanel componentsPanel = new JPanel(new BorderLayout());
        componentsPanel.add(supplyDemandPanel, BorderLayout.NORTH);
        componentsPanel.add(buttonPanel, BorderLayout.CENTER);
        componentsPanel.add(totalCostPanel, BorderLayout.SOUTH);
        JPanel middlePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 70, 10); 

        gbc.gridx = 0;
        gbc.gridy = 0;
        middlePanel.add(supplyDemandPanel, gbc);

        gbc.gridy = 1;
        middlePanel.add(buttonPanel, gbc);

        gbc.gridy = 2;
        middlePanel.add(totalCostPanel1, gbc);

        gbc.gridy = 3;
        middlePanel.add(totalCostPanel, gbc);

        panel3.add(headingLabel, BorderLayout.NORTH);
        panel3.add(tableScrollPane, BorderLayout.CENTER);
        panel3.add(middlePanel, BorderLayout.SOUTH); 

        add(panel3, BorderLayout.CENTER);
        revalidate();
        repaint();
}
    
       
    


    public void displayResults() {
        rowPenalty = new int[rows + 1];
        columnPenalty = new int[columns + 1];
        supply = new int[rows];
        demand = new int[columns];

        for (int i = 0; i < rows; i++) {
            try {
                supply[i] = Integer.parseInt(supplyFields[i].getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid integers in all supply fields.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        for (int i = 0; i < columns; i++) {
            try {
                demand[i] = Integer.parseInt(demandFields[i].getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid integers in all demand fields.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        System.out.println("Cost Matrix:");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(cost[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("Supply:");
        for (int i = 0; i < rows; i++) {
            System.out.print(supply[i] + " ");
        }
        System.out.println();

        System.out.println("Demand:");
        for (int i = 0; i < columns; i++) {
            System.out.print(demand[i] + " ");
        }
        System.out.println();

        //ACTUAL IMPLEMENTATION OF VAM
        fixIfUnbalanced();
        computeTransportationCost();
        totalCost1 = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                totalCost1 += allotment[i][j] * cost[i][j];
            }
        }

    }
    public void fixIfUnbalanced() {
        int totalSup = 0, totalDem = 0;
        for (int i = 0; i < rows; i++) {
            totalSup += supply[i];
        }
        for (int i = 0; i < columns; i++) {
            totalDem += demand[i];
        }
        if (totalSup < totalDem) {
            rows += 1;
            supply = Arrays.copyOf(supply, rows);
            supply[rows - 1] = totalDem - totalSup;
            allotment = new int[rows][columns]; // Initialize the allotment array with the new size
        } else if (totalDem < totalSup) {
            columns += 1;
            demand = Arrays.copyOf(demand, columns);
            demand[columns - 1] = totalSup - totalDem;
            allotment = new int[rows][columns]; // Initialize the allotment array with the new size
        }
    }

    public void calcPenalties() {
        // Calculate row penalties
        for (int row = 0; row < rows; row++) {
            if (supply[row] == 0) {
                continue;
            }

            int[] tArr = new int[columns + 1];
            int nonZeroDemCount = 0;

            for (int col = 0; col < columns; col++) {
                if (demand[col] != 0) {
                    tArr[++nonZeroDemCount] = cost[row][col];
                }
            }

            sort(tArr, nonZeroDemCount);

            if (nonZeroDemCount != 1) {
                rowPenalty[row] = Math.abs(tArr[1] - tArr[2]);
            } else {
                rowPenalty[row] = tArr[1];
            }
        }

        // Calculate column penalties
        for (int col = 0; col < columns; col++) {
            if (demand[col] == 0) {
                continue;
            }

            int[] tArr = new int[rows + 1];
            int nonZeroSupCount = 0;

            for (int row = 0; row < rows; row++) {
                if (supply[row] != 0) {
                    tArr[++nonZeroSupCount] = cost[row][col];
                }
            }

            sort(tArr, nonZeroSupCount);

            if (nonZeroSupCount != 1) {
                columnPenalty[col] = Math.abs(tArr[1] - tArr[2]);
            } else {
                columnPenalty[col] = tArr[1];
            }
        }
    }

    public void sort(int[] arr, int size) {
        for (int i = 1; i < size; i++) {
            int index = i;
            for (int j = i + 1; j <= size; j++) {
                if (arr[j] < arr[index]) {
                    index = j;
                }
            }
            int temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }

    public int min(int num1, int num2) {
        return num1 < num2 ? num1 : num2;
    }
    public void findMaxPenalty(int[] rowColumnNum) {
        rowColumnNum[0] = -1; // Row index
        rowColumnNum[1] = -1; // Column index
        int maxPenalty = -1;

        // Searching for the maximum row penalty
        for (int row = 0; row < rows; row++) {
            if (supply[row] != 0 && rowPenalty[row] > maxPenalty) {
                rowColumnNum[0] = row;
                maxPenalty = rowPenalty[row];
            }
        }

        // Searching for the maximum column penalty
        for (int col = 0; col < columns; col++) {
            if (demand[col] != 0 && columnPenalty[col] > maxPenalty) {
                rowColumnNum[0] = -1; // Reset row index
                rowColumnNum[1] = col;
                maxPenalty = columnPenalty[col];
            }
        }
    }



    public boolean demandFulfilled() {
        boolean Dem = true, Sup = true;
        // Checking if all the elements of the demand matrix are 0
        for (int i = 0; i < columns; i++) {
            if (demand[i] != 0) {
                Dem = false;
                break;
            }
        }
        // Checking if all the elements of the supply matrix are 0
        for (int i = 0; i < rows; i++) {
            if (supply[i] != 0) {
                Sup = false;
                break;
            }
        }
        return Dem && Sup;
    }
    public void computeTransportationCost() {
        int iteration = 0;
        while (!demandFulfilled()) {
            calcPenalties();
            int[] rowColumnNum = new int[2];
            findMaxPenalty(rowColumnNum);

            // If a row has the maximum penalty value
            if (rowColumnNum[0] != -1) {
                int row = rowColumnNum[0];
                int minCost = Integer.MAX_VALUE;
                int minCol = -1;

                // Find the minimum cost in the row
                for (int col = 0; col < columns; col++) {
                    if (demand[col] != 0 && cost[row][col] < minCost) {
                        minCost = cost[row][col];
                        minCol = col;
                    }
                }

                if (minCol != -1) {
                    int minAllotment = min(supply[row], demand[minCol]);
                    allotment[row][minCol] = minAllotment;
                    supply[row] -= minAllotment;
                    demand[minCol] -= minAllotment;
                }
            } else if (rowColumnNum[1] != -1) {
                int col = rowColumnNum[1];
                int minCost = Integer.MAX_VALUE;
                int minRow = -1;

                // Find the minimum cost in the column
                for (int row = 0; row < rows; row++) {
                    if (supply[row] != 0 && cost[row][col] < minCost) {
                        minCost = cost[row][col];
                        minRow = row;
                    }
                }

                if (minRow != -1) {
                    int minAllotment = min(supply[minRow], demand[col]);
                    allotment[minRow][col] = minAllotment;
                    supply[minRow] -= minAllotment;
                    demand[col] -= minAllotment;
                }
            }

            iteration += 1;

        }

        totalCost = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                totalCost += allotment[i][j] * cost[i][j];
            }
        }
        System.out.println("\n\n");

        System.out.println("Total transportation cost using VAM = " + totalCost);
        totalCostLabel1.setText("Cost after VAM: " + totalCost);
        SSM();


    }
    public static void SSM() {
        try
        {
            int smallest=0,i,k;
            int[] arr=new int[7];//rows+column-1
            int[] arr2=new int[7];//rows+column-1
            int diff=0;
            int counter=0;

            allotment[0][0]=3;
            allotment[0][1]=2;

            allotment[3][1]=0;
            allotment[3][0]=4;
            System.out.println("Basic Feasible Solution Matrix");
            for( i=0;i<rows;i++)
            {
                for(int j=0;j<columns;j++)
                {
                    System.out.print(allotment[i][j]+"   ");
                }
                System.out.println();
            }

            do{
                    int x = 0;
                    for (i = 0; i < 4; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (allotment[i][j] == 0) {
                                looper(i, j, x);
                                x++;
                            }
                        }
                    }
                    System.out.println("Loops completed !!");
                    for(i=0;i<4+3-1;i++)
                    {
                        arr[i]=netcharge(i);
                        //System.out.println(arr[i]);
                    }
                    int loc=0;
                    smallest=0;
                    for(i=0;i<4+3-1;i++) //k<=numRows-numCols-1
                    {

                        if(arr[i]<smallest)
                        {
                            smallest=arr[i];
                            loc=i;
                        }
                    }
                    if(smallest>=0) {
                        int optcos=0;
                        for(i=0;i<4;i++)//rows
                        {
                            for(int j=0;j<3;j++)// columns
                                optcos+=cost[i][j]*allotment[i][j];
                        }
                        if(totalCost > optcos)
                        {
                            totalCostLabel.setText("Cost after applying Stepping stone method: " + optcos);
                        }
                        else
                        {
                            totalCostLabel.setText("No Optimal Solution");
                        }
                        System.out.println("Not Better Solution to be found than "+optcos);
                        return;
                    }
                    else
                    {
                        k=0;
                        counter=0;
                        i=0;
                        while(true)
                        {
                            if(path[loc][i].r==-1)
                                break;
                            else if(counter%2!=0)
                                arr2[k++]=allotment[path[loc][i].r][path[loc][i].c]*-1;
                            i++;
                            counter++;
                        }
                        diff=arr2[0];
                        for(i=0;i<k;i++)
                        {
                            if(arr2[i]>diff)
                            {
                                diff=arr[i];
                            }
                        }
                        diff=abs(diff);
                        System.out.println(diff);
                        i=0;
                        counter=0;
                        while(true)
                        {
                            if(path[loc][i].r==-1)
                                break;
                            else if(counter%2==0)
                                allotment[path[loc][i].r][path[loc][i].c]=allotment[path[loc][i].r][path[loc][i].c]+diff;
                            else
                                allotment[path[loc][i].r][path[loc][i].c]=allotment[path[loc][i].r][path[loc][i].c]-diff;
                            i++;
                            counter++;
                        }
                        System.out.println("New Allocations");
                        for(i=0;i<4;i++)//rows
                        {
                            for(int j=0;j<3;j++) //rows
                            {
                                System.out.print(allotment[i][j]+"    ");
                            }
                            System.out.println();
                        }
                    }
            }while(true);
        }catch(Exception e)
        {
            totalCostLabel.setText("No Optimal Solution");
        }
    }



    private static void looper(int og_i, int og_j, int path_r) {
        try
        {
            //roc =0 then columns searching else row searching
            int roc = 0;
            path[path_r][0] = new Path(og_i, og_j);
            //  System.out.println("Check Point 1");

            System.out.println("Making Loop "+(path_r+1)+" ....");
            if (possibilityChecker(og_i, og_j, path_r, 0, 0, og_i, og_j,0) == 0) {
                possibilityChecker(og_i, og_j, path_r, 0, 1, og_i, og_j,0);

            } else
                return;
        }catch(Exception e)
        {
            totalCostLabel.setText("No Optimal Solution");
        }
    }

    public static int possibilityChecker(int og_i, int og_j, int path_r, int path_c, int roc, int pre_i, int pre_j,int exe) {
        try
        {
            int[] poss = new int[5];
            int x = 0, flag = -1;
            //System.out.println("Check Point 2");
            switch (roc) {
                case 0: {
                    //      System.out.println("Column Searching\n Allocation At");
                    for (int k = 0; k < 4; k++) // rows
                    {
                        if (allotment[k][pre_j] != 0 && pre_i != k) {

                            poss[x++] = k;
                            //    System.out.println(poss[x - 1] + 1);
                        } else {
                            if ((k == og_i) && (pre_j == og_j) && path_c != 0 && exe != 1)
                                flag = k;
                            else
                                continue;
                        }
                    } // searching
                    if (x == 0 && flag == -1) {
                        return 0;
                    } else if (flag != -1) {
                        if (x == 1) {

                            path[path_r][path_c+1] = new Path(-1, -1);
                            path[path_r][path_c] = new Path(poss[x - 1], pre_j);
                            System.out.println("Loop End Found ");
                        } else {
                            // path_c++;
                            path[path_r][path_c + 1] = new Path(-1, -1);
                            //   path[path_r][path_c - 1] = new Path(pre_i, pre_j);
                            System.out.println("Loop End Found");
                        }
                        return 1;
                    } else {
                        path_c++;
                        if (x == 2) {
                            if (checkBetween(poss[x - 1], pre_i, poss[0]) == 1) {
                                path_c += 1;
                                if (possibilityChecker(og_i, og_j, path_r, path_c, 1, poss[x - 1], pre_j,0) != 0) {
                                    path[path_r][path_c] = new Path(poss[x - 1], pre_j);
                                    path[path_r][path_c-1] = new Path(poss[0], pre_j);
                                    return 1;
                                }}
                            else if (checkBetween(poss[0], pre_i, poss[x-1]) == 1) {
                                path_c += 1;
                                if (possibilityChecker(og_i, og_j, path_r, path_c, 0, poss[x - 1], pre_j,1) != 0) {
                                    path[path_r][path_c] = new Path(poss[0], pre_j);
                                    path[path_r][path_c - 1] = new Path(poss[x-1], pre_j);
                                    return 1;
                                }
                            }
                            else if (possibilityChecker(og_i, og_j, path_r, path_c, 1, poss[0], pre_j,0) != 0)
                            {
                                path[path_r][path_c] = new Path(poss[0], pre_j);
                                return 1;
                            } else if (possibilityChecker(og_i, og_j, path_r, path_c, 1, poss[x - 1], pre_j,0) != 0) {

                                path[path_r][path_c] = new Path(poss[x - 1], pre_j);
                                return 1;
                            } else return 0;

                        } else {
                            if (possibilityChecker(og_i, og_j, path_r, path_c, 1, poss[x - 1], pre_j,0) != 0) {
                                path[path_r][path_c] = new Path(poss[x - 1], pre_j);
                                return 1;
                            }
                            else
                                return 0;

                        }
                    }
                    break;
                }
                case 1: {
                    //    System.out.println("Row Searching\n Allocation At");
                    for (int k = 0; k < 3; k++) // columns
                    {
                        if (allotment[pre_i][k] != 0 && (pre_j != k)) {

                            poss[x++] = k;
                            //    System.out.println(poss[x - 1] + 1);
                        } else {
                            if ((pre_i == og_i) && (k == og_j) && path_c != 0 && exe!=1)
                                flag = k;
                            else
                                continue;
                        }
                    }


                    // seraching
                    if (x == 0 && flag == -1) {
                        return 0;
                    } else if (flag != -1) {
                        if (x == 1) {
                            path_c++;
                            path[path_r][path_c+1] = new Path(-1, -1);
                            path[path_r][path_c] = new Path(pre_i, poss[x - 1]);
                            System.out.println("Loop End Found ");
                        } else {
                            // path_c++;
                            path[path_r][path_c + 1] = new Path(-1, -1);
                            //   path[path_r][path_c - 1] = new Path(pre_i, pre_j);
                            System.out.println("Loop End FOund");
                        }
                        return 1;
                    } else {
                        path_c++;
                        if (x == 2) {
                            if (checkBetween(poss[x - 1], pre_j, poss[0]) == 1) {
                                path_c += 1;
                                if (possibilityChecker(og_i, og_j, path_r, path_c, 1, pre_i, poss[x - 1],0) != 0) {
                                    path[path_r][path_c] = new Path(pre_i, poss[x - 1]);
                                    path[path_r][path_c - 1] = new Path(pre_i, poss[0]);
                                    return 1;
                                }
                            }else if (checkBetween(poss[0], pre_j, poss[x-1]) == 1) {
                                path_c += 1;
                                if (possibilityChecker(og_i, og_j, path_r, path_c, 1, pre_i, poss[x - 1],1) != 0) {
                                    path[path_r][path_c] = new Path(pre_i, poss[0]);
                                    path[path_r][path_c - 1] = new Path(pre_i, poss[x-1]);
                                    return 1;
                                }
                            }
                            else if (possibilityChecker(og_i, og_j, path_r, path_c, 0, pre_i, poss[0],0) != 0) {
                                path[path_r][path_c] = new Path(pre_i, poss[0]);
                                return 1;
                            } else if (possibilityChecker(og_i, og_j, path_r, path_c+1, 0, pre_i, poss[x-1],0) != 0) {
                                path[path_r][path_c] = new Path(pre_i, poss[x-1]);
                                return 1;
                            } else return 0;

                        } else {
                            if (possibilityChecker(og_i, og_j, path_r, path_c, 0, pre_i, poss[x - 1],0) != 0) {
                                path[path_r][path_c] = new Path(pre_i, poss[x - 1]);
                                return 1;
                            } else
                                return 0;
                        }
                    }
                }
            }

            return 1;
        }catch(Exception e)
        {
            totalCostLabel.setText("No Optimal Solution");
            return 0;
        }
    }


    public static int checkBetween(int b1 ,int b2,int between)
    {
        try 
        {
            if(between<b1 && between>b2)
                return 1;
            else if(between>b1 && between<b2)
                return 1;
            else
                return 0;
        }catch(Exception e)
        {
            totalCostLabel.setText("No Optimal Solution");
            return 0;
        }
    }  



    public static int netcharge(int path_no)
    {   
        try
        {
            int sum=0;
            int counter=0;
            int i=0;
            while(true)
            {
                if(path[path_no][i].r==-1)
                    break;
                else if(counter%2==0)
                    sum=sum+cost[path[path_no][i].r][path[path_no][i].c];
                else
                    sum=sum+cost[path[path_no][i].r][path[path_no][i].c]*-1;
                counter++;
                i++;
            }
            return sum;
        }catch(Exception e)
        {
            totalCostLabel.setText("No Optimal Solution");
            return 0;
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TransportProblemGUI transportProblemGUI = new TransportProblemGUI();
            }
        });
    }
}