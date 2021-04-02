package gameoflife;

import acm.graphics.*;
import acm.program.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameOfLife extends GraphicsProgram
{
	final String TITLE = "Game Of Life";
	final int WINDOW_MARGIN = 10;
	final int CELL_SIZE = 10;
	final int PADDING_SIZE = 2;
	final int ROWS = 75;
	final int COLS = 75;
	final Color EMPTY_CELL = Color.WHITE;
	final Color LIVE_CELL = Color.BLACK;
	final int GRID_SIZE = (COLS * CELL_SIZE) + (COLS * PADDING_SIZE);
	final int BORDER_ADJUST = 14;
	final int MENU_ADJUST = 81;
	final int WINDOW_HEIGHT = GRID_SIZE + (WINDOW_MARGIN * 2) + BORDER_ADJUST + MENU_ADJUST;
	final int WINDOW_WIDTH = GRID_SIZE + (WINDOW_MARGIN * 2) + BORDER_ADJUST;
	final GRect[][] cells = new GRect[ROWS][COLS];
	final int[][] test = {{0,-1},{0,2},{-1,0},{-1,1},{-1,2},{1,-2},{1,2}};
	final int[][] acorn = {{0,0},{-1,-2},{1,-2},{1,-3},{1,1},{1,2},{1,3}};
	final int[][] dieHard = {{0,-2},{0,-3},{1,-2},{-1,3},{1,2},{1,3},{1,4}};
	final int[][] pentaDecathlon = {{0,0},{0,-1},{-1,-1},{-2,-1},{-3,-1},{1,-1},{2,-1},{2,0},{2,1},{1,1},{0,1},
								   {-1,0},{-2,0},{-3,0},{-1,1},{-2,1},{-3,1},{-4,1},{-5,1},{-5,0},{-5,-1},{-4,-1}};
	final String[] PRESET_NAMES = {"Reset","Test","Acorn","Die Hard","Penta Dec"};
    final int[][][] PRESETS = {{},test,acorn,dieHard,pentaDecathlon};
	int runDelay = 200;
	int currGen = 0;
	int currPop = 0;
	boolean isReset = false;
	int gridPreset = 0;
	JPanel uiContainer;
	JButton startButton;
	JButton nextButton;
	JButton resetButton;
	JTextField textField;
	JComboBox presets;
	//JButton testButton;
	//JButton acornButton;
	
	public static void main(String[] args) 
	{
		new GameOfLife().start();
	}
	
	@Override
    public void init() 
	{
		getMenuBar().setVisible(false); // gets rid of menu (file, edit etc)
		setSize (WINDOW_WIDTH,WINDOW_HEIGHT);
        setTitle(TITLE);
		addPanel();
        addStartButton();
		addNextButton();
		addTextField();
		//addResetButton();
		initializeGrid();
		reset();
		addPresets();
		//createPresetButton("Test", test, 7);   // creates button for the test pattern of PDF
		//createPresetButton("Acorn", acorn, 7); // creates button for the acorn pattern
	}
	@Override
	public void run()
	{
		while(true)
		{
			if(isReset)
			{
				nextGeneration(cells,ROWS,COLS);
			}
			pause(runDelay);
		}
	}
	private void addPanel()
	{
		uiContainer = new JPanel();
		uiContainer.setSize(getWidth(), 40);
		add(uiContainer, 0, getHeight() - 60);
		uiContainer.setBackground(Color.WHITE);
	}
	private void addStartButton()
	{
		startButton = new JButton("Start");
		if (currPop == 0)
				startButton.setEnabled(false);
        startButton.addActionListener((ActionEvent ae) -> {
			isReset = !isReset;
			
			if (isReset)
			{
				startButton.setText("Pause");
				nextButton.setEnabled(false);
			}
			else
			{
				startButton.setText("Start");
				nextButton.setEnabled(true);
			}
		});

        uiContainer.add(startButton);
	}
	private void addNextButton()
	{
		nextButton = new JButton("Next");
		nextButton.setPreferredSize(new Dimension(70, 25));
		nextButton.setEnabled(false);
		nextButton.addActionListener((ActionEvent ae) -> 
		{
			nextGeneration(cells, ROWS, COLS);
			updatePop();
		});
		uiContainer.add(nextButton);
	}
	private void addTextField()
	{
		textField = new JTextField();
		textField.setEditable(false);
        textField.setPreferredSize(new Dimension(200, 24));
        textField.setHorizontalAlignment(JTextField.CENTER);
        updatePop();
        uiContainer.add(textField);
		
	}
	// using reset in JComboBox
	/*private void addResetButton()
	{
		resetButton = new JButton("Reset");
        uiContainer.add(resetButton);
		resetButton.addActionListener((ActionEvent ae) -> {
			reset();
		});
	}*/
	private void initializeGrid()
	{
		int cellX = WINDOW_MARGIN;
		int cellY = WINDOW_MARGIN;
		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLS; j++)
			{
				GRect cell = new GRect (cellX, cellY, CELL_SIZE, CELL_SIZE);
				if(i == 0 || i == ROWS - 1 || j == COLS - 1 || j == 0)
					cell.setColor(EMPTY_CELL);
				else
				{
					cell.setFilled(true);
					cell.setFillColor(EMPTY_CELL);
				}
				cell.addMouseListener(new MouseAdapter()	
				{
					@Override
					public void mouseReleased(MouseEvent me)
					{
						if(cell.getFillColor() == EMPTY_CELL)
						{
							if(!isReset) // keeps you from toggling/clicking cells while its running through generations
							{
							cell.setFillColor(LIVE_CELL);
							currPop++;
							startButton.setEnabled(true);
							}
						}
						else
						{
							cell.setFillColor(EMPTY_CELL);
							currPop--;
						}
						updatePop();
					}
				});
				add(cell);
				cells[i][j] = cell;
				cellX += (GRID_SIZE / COLS);
			}
			cellY += CELL_SIZE + PADDING_SIZE;
            cellX = WINDOW_MARGIN;
		}
	}
	private void reset()
	{
			clearGrid();
			isReset = false;
			startButton.setText("Start");
			nextButton.setEnabled(false);
			startButton.setEnabled(false);
			currPop = 0;
			currGen = 0;
			updatePop();
	}
	private void clearGrid()
	{
		for (int i = 0; i < ROWS; i++)
        {
            for (int j = 0; j < COLS; j++)
            {
                cells[i][j].setFillColor(EMPTY_CELL);
            }
        }
	}
	private void updatePop()
	{
		textField.setText(" Generation: " + currGen + "  Population: " + currPop + " ");
	}
	private int countNeighbors(GRect grid[][], int r, int c)
    {
        int count = 0;

        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                if (grid[r + i][c + j].getFillColor() == LIVE_CELL && !(i == 0 && j == 0))
                    count++;

        return count;
    }
	private void nextGeneration(GRect grid[][], int rows, int cols)
	{
		boolean[][] nextGen = new boolean[rows][cols];
		for(int i = 1; i < rows - 1; i++)
		{
			for(int j = 1; j < cols - 1; j++)
			{
				boolean alive = grid[i][j].getFillColor() == LIVE_CELL;
				int count = countNeighbors(grid,i,j);
				if(count >= 4 && alive)
					nextGen[i][j] = false;
				else if(count >= 2 && alive)
					nextGen[i][j] = true;
				else if(count >= 0 && alive)
					nextGen[i][j] = false;
				else if(count == 3 && !alive)
					nextGen[i][j] = true;
			}
		}
		currPop = 0;
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < cols; j++)
			{
				if(i == 0 || i == rows - 1 || j == cols - 1 || j == 0)
					grid[i][j].setColor(EMPTY_CELL);
				else if(nextGen[i][j] == true)
				{
					grid[i][j].setFillColor(LIVE_CELL);
					currPop++;
				}
				else
					grid[i][j].setFillColor(EMPTY_CELL);
			}
		}
		currGen++;
		updatePop();
	}
	private void fillInPattern(int[][] pattern) 
	{
		boolean[][] testPattern = new boolean[ROWS][COLS];
		
		for(int i = 0; i < ROWS; i++)
			for(int j = 0; j < COLS; j++)
			{
				testPattern[i][j] = false;
			}
		
		int midCol = COLS / 2;
		int midRow = ROWS / 2;
		
		for(int i = 0; i < pattern.length; i++)
			testPattern[midRow + pattern[i][0]][midCol + pattern[i][1]] = true;
		
		for(int i = 0; i < ROWS; i++)
			for(int j = 0; j < COLS; j++)
			{
				if(testPattern[i][j])
				cells[i][j].setFillColor(LIVE_CELL);
			}
	}
	
	// Was using this method to create buttons but decided to use JComboBox after # of buttons got out of hand
/*	public void createPresetButton(String name, int[][] pattern, int pop)
    {
        JButton newButton = new JButton(name);
        newButton.addActionListener((ActionEvent ae) -> 
        {
            reset();
            fillInPattern(pattern);
            currGen = 0;
            currPop = pop;
            updatePop();
            newButton.setEnabled(true);
        });
        uiContainer.add(newButton);
    }
*/
	private void addPresets()
    {
        presets = new JComboBox();

        for (int i = 0; i < PRESETS.length; i++)
        {
            presets.addItem(PRESET_NAMES[i]);
        }

        presets.addActionListener((ActionEvent ae) ->
        {
            if (gridPreset != presets.getSelectedIndex())
            {
                gridPreset = presets.getSelectedIndex();
                reset();
                initializeGrid();
				startButton.setEnabled(true);
				switch(presets.getSelectedIndex())
				{
					case 0 -> { clearGrid(); startButton.setEnabled(false);} // when choosing Reset, clears grid and grays out start button
					case 1 -> fillInPattern(test);  // sets test PDF pattern
					case 2 -> fillInPattern(acorn); // acorn pattern, goes for very long time
					case 3 -> fillInPattern(dieHard);
					case 4 -> fillInPattern(pentaDecathlon);
				}
            }
        });

        presets.setSelectedIndex(gridPreset);
        uiContainer.add(presets);
    }
}
