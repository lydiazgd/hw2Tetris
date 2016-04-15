// Board.java
package tetris;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = false;
	boolean committed;
	private int[] widths;
	private int[] heights;
	private boolean [][]backgrid;
	private int []backwidths;
	private int []backheights;
	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
				grid[i][j]=false;
		widths=new int[height];
		heights=new int [width];
		backgrid=new boolean[width][height];
		backwidths=new int[height];
		backheights=new int[width];
		// YOUR CODE HERE
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {
		int maxheight=0;
		for(int i=0;i<width;i++)
			if(heights[i]>=maxheight)
				maxheight=heights[i];
		return maxheight; // YOUR CODE HERE
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			int []checkwidths=new int[height];
			int []checkheights=new int[width];
			int checkmaxheight=0;
			for(int i=0;i<width;i++)
				for(int j=0;j<height;j++){
					if(grid[i][j]){
						checkwidths[j]++;
						checkheights[i]++;
						if(checkheights[i]>=checkmaxheight)
							checkmaxheight=checkheights[i];
					}

				}
			// YOUR CODE HERE
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int max=0;
		int[] miny=piece.getSkirt();
		for(int i=0;i<miny.length;i++){
			if(max<heights[x]-miny[i])
				max=heights[x]-miny[i];
			x++;
		}
		return max; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heights[x] ; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		return widths[y]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		return grid[x][y]; // YOUR CODE HERE
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Cop
	 ies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
		backup();
		int result = PLACE_OK;
		int x1,y1;
		for(int i=0;i<piece.getBody().length;i++){
			System.out.println("x: " + x + ", y:" + y);
			x1 = piece.getBody()[i].x + x;
			y1 = piece.getBody()[i].y + y;
			if(x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) {
				System.out.println("x1: " + x1 + ", y1: " + y1);
				if (!grid[x1][y1]) {
					grid[x1][y1] = true;
					widths[y1]++;
					if (heights[x1] < y1 + 1)
						heights[x1]=y1 + 1;
				}
				else {
					return PLACE_BAD;
				}
			}
			else{
				return PLACE_OUT_BOUNDS;
			}
		}
//		for(int i=y;i<height;i++){
		for(int i=y;i<y+piece.getHeight();i++){
			if(widths[i]==width)
				result=PLACE_ROW_FILLED;
		}
		committed=false;
		sanityCheck();
		// YOUR CODE HERE
		
		return result;
	}
	
	
	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		backup();
		int rowsCleared = 0;
		int ture=0;
		for(int highest=0;highest<height;highest++)
			for(int widthest=0;widthest<width;widthest++){
				if(grid[widthest][highest]) {
					ture = highest;
					break;
				}
			}
		for(int i=0;i<=ture;i++) {
			int test=0;
			for (int j = 0; j < width; j++) {
				if (grid[j][i]) {
					test++;
				}
			}
			if(test==width){
				rowsCleared++;
//				widths[i]=0;
				for(int row=0;row<width;row++)
					heights[row]--;
				for(int clear=i;clear<ture;clear++){
					for(int clear2=0;clear2<width;clear2++)
						grid[clear2][clear]=grid[clear2][clear+1];
				}
				for(int clear3=0;clear3<width;clear3++)
					grid[clear3][ture]=false;
				for(int x=0;x<height;x++) {
					widths[x]=0;
					for (int y = 0; y < width; y++) {
						if (grid[y][x])
							widths[x]++;
					}
				}
				i=i-1;
				ture--;
			}


		}
		// YOUR CODE HERE
		committed=false;
		sanityCheck();
		return rowsCleared;
	}



	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		for(int i=0;i<width;i++)
			System.arraycopy(backgrid[i],0,grid[i],0,grid[i].length);
		System.arraycopy(backwidths,0,widths,0,height);
		System.arraycopy(backheights,0,heights,0,width);
		committed=true;
		sanityCheck();
		// YOUR CODE HERE
	}
	private void backup(){
		for(int i=0;i<width;i++)
			System.arraycopy(grid[i],0,backgrid[i],0,backgrid[i].length);
		System.arraycopy(widths,0,backwidths,0,height);
		System.arraycopy(heights,0,backheights,0,width);
	}
	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
	}


	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


