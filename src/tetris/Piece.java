// Piece.java
package tetris;

import java.util.*;

/**
 An immutable representation of a tetris piece in a particular rotation.
 Each piece is defined by the blocks that make up its body.
 
 Typical client code looks like...
 <pre>
 Piece pyra = new Piece(PYRAMID_STR);		// Create piece from string
 int width = pyra.getWidth();			// 3
 Piece pyra2 = pyramid.computeNextRotation(); // get rotation, slow way
 
 Piece[] pieces = Piece.getPieces();	// the array of root pieces
 Piece stick = pieces[STICK];
 int width = stick.getWidth();		// get its width
 Piece stick2 = stick.fastRotation();	// get the next rotation, fast way
 </pre>
*/
public class Piece {
	// Starter code specs out a few basic things, leaving
	// the algorithms to be done.
	private TPoint[] body;
	private int[] skirt=new int [8];
	private int width;
	private int height;
	private Piece next; // "next" rotation

	static private Piece[] pieces;	// singleton static array of first rotations

	/**
	 Defines a new piece given a TPoint[] array of its body.
	 Makes its own copy of the array and the TPoints inside it.
	*/
	public Piece(TPoint[] points) {
		int maxwidth=0;
		this.body=points;
		for(int i=0;i<points.length;i++){
			if(points[i].x>=maxwidth)
				maxwidth=points[i].x;
		}
		this.width=maxwidth+1;
		int maxheight=0;
		for(int i=0;i<points.length;i++){
			if(points[i].y>=maxheight)
				maxheight=points[i].y;
		}
		this.height=maxheight+1;
		int standard=0;
		int length=0;
		int test=-1;
		for(int i=0;i<points.length;i++) {
			if (points[i].x != test) {
				test=points[i].x;
				length++;
			}
		}
		int[] skirt2=new int[length];
		int[] skirt1=new int[length];
		int test1=-1;
		int length1=0;
		for(int i=0;i<points.length;i++){
			if (points[i].x != test1) {
				test1=points[i].x;
				skirt2[length1]=points[i].x;
				length1++;
			}
		}
		Arrays.sort(skirt2);
		for(int i=0;i<skirt2.length;i++){
		     standard=skirt2[i];
			int  miny=maxheight;
			for(int j=0;j<points.length;j++){
				if(points[j].x==standard){
					if(points[j].y<=miny)
						miny=points[j].y;
				}
			}
			skirt1[i]=miny;
		}

		this.skirt=skirt1;
	}
	
	/**
	 * Alternate constructor, takes a String with the x,y body points
	 * all separated by spaces, such as "0 0  1 0  2 0	1 1".
	 * (provided)
	 */
	public Piece(String points) {this(parsePoints(points));}

	/**
	 Returns the width of the piece measured in blocks.
	*/
	public int getWidth() {
		return width;
	}

	/**
	 Returns the height of the piece measured in blocks.
	*/
	public int getHeight() {
		return height;
	}

	/**
	 Returns a pointer to the piece's body. The caller
	 should not modify this array.
	*/
	public TPoint[] getBody() {return body;}

	/**
	 Returns a pointer to the piece's skirt. For each x value
	 across the piece, the skirt gives the lowest y value in the body.
	 This is useful for computing where the piece will land.
	 The caller should not modify this array.
	*/
	public int[] getSkirt() {
		return skirt;
	}

	
	/**
	 Returns a new piece that is 90 degrees counter-clockwise
	 rotated from the receiver.
	 */
	public Piece computeNextRotation() {
		StringBuilder a=new StringBuilder();
		int[] replacement=new int[body.length];
		int middle=0;
		for(int i=0;i<body.length;i++){
			replacement[i]=body[i].y;
		}
		for(int i=0;i<body.length;i++){
			for(int j=i+1;j<body.length;j++){
				if(replacement[i]>replacement[j]){
					middle=replacement[j];
					replacement[j]=replacement[i];
					replacement[i]=middle;
				}
			}

		}
		int temp=0;
		int first=-1;
		for(int i=0;i<replacement.length;i++){
			if(first!=replacement[i]){
				first=replacement[i];
				temp++;
			}
		}
		int[] replacement2=new int[temp];
		int first1=-1;
		int temp1=0;
		for(int i=0;i<replacement.length;i++){
			if(first1!=replacement[i]){
				replacement2[temp1]=replacement[i];
				first1=replacement[i];
				temp1++;
			}
		}
		int add=0;
		for(int i=0;i<temp1;i++)
			for(int j=0;j<body.length;j++){
				if(body[j].y==replacement2[i]){
					a.append(replacement2[temp1-1-i]);
					a.append(" ");
					a.append(body[j].x);
					a.append(" ");
				}
			}
		String string=a.toString();
		Piece piece=new Piece(string);
		return piece; // YOUR CODE HERE
	}

	/**
	 Returns a pre-computed piece that is 90 degrees counter-clockwise
	 rotated from the receiver.	 Fast because the piece is pre-computed.
	 This only works on pieces set up by makeFastRotations(), and otherwise
	 just returns null.
	*/	
	public Piece fastRotation() {return next;}
	


	/**
	 Returns true if two pieces are the same --
	 their bodies contain the same points.
	 Interestingly, this is not the same as having exactly the
	 same body arrays, since the points may not be
	 in the same order in the bodies. Used internally to detect
	 if two rotations are effectively the same.
	*/
	public boolean equals(Object obj) {
		boolean define=false;
		// standard equals() technique 1
		if (obj == this)
			return true;

		// standard equals() technique 2
		// (null will be false)
		if (!(obj instanceof Piece))
			return false;
		Piece other = (Piece)obj;
		int test=0;
		for(int i=0;i<this.getBody().length;i++)
			for(int j=0;j<other.getBody().length;j++){
				if((this.getBody()[i].x==other.getBody()[j].x)&&(this.getBody()[i].y==other.getBody()[j].y))
					test++;
			}
		if(test==this.getBody().length)
			define=true;
		return define;
	}


	// String constants for the standard 7 tetris pieces
	public static final String STICK_STR	= "0 0	0 1	 0 2  0 3";
	public static final String L1_STR		= "0 0	0 1	 0 2  1 0";
	public static final String L2_STR		= "0 0	1 0 1 1	 1 2";
	public static final String S1_STR		= "0 0	1 0	 1 1  2 1";
	public static final String S2_STR		= "0 1	1 1  1 0  2 0";
	public static final String SQUARE_STR	= "0 0  0 1  1 0  1 1";
	public static final String PYRAMID_STR	= "0 0  1 0  1 1  2 0";
	
	// Indexes for the standard 7 pieces in the pieces array
	public static final int STICK = 0;
	public static final int L1	  = 1;
	public static final int L2	  = 2;
	public static final int S1	  = 3;
	public static final int S2	  = 4;
	public static final int SQUARE	= 5;
	public static final int PYRAMID = 6;
	
	/**
	 Returns an array containing the first rotation of
	 each of the 7 standard tetris pieces in the order
	 STICK, L1, L2, S1, S2, SQUARE, PYRAMID.
	 The next (counterclockwise) rotation can be obtained
	 from each piece with the {@link #fastRotation()} message.
	 In this way, the client can iterate through all the rotations
	 until eventually getting back to the first rotation.
	 (provided code)
	*/
	public static Piece[] getPieces() {
		// lazy evaluation -- create static array if needed
		if (Piece.pieces==null) {
			// use makeFastRotations() to compute all the rotations for each piece
			Piece.pieces = new Piece[] {
				makeFastRotations(new Piece(STICK_STR)),
				makeFastRotations(new Piece(L1_STR)),
				makeFastRotations(new Piece(L2_STR)),
				makeFastRotations(new Piece(S1_STR)),
				makeFastRotations(new Piece(S2_STR)),
				makeFastRotations(new Piece(SQUARE_STR)),
				makeFastRotations(new Piece(PYRAMID_STR)),
			};
		}
		
		
		return Piece.pieces;
	}
	


	/**
	 Given the "first" root rotation of a piece, computes all
	 the other rotations and links them all together
	 in a circular list. The list loops back to the root as soon
	 as possible. Returns the root piece. fastRotation() relies on the
	 pointer structure setup here.
	*/
	/*
	 Implementation: uses computeNextRotation()
	 and Piece.equals() to detect when the rotations have gotten us back
	 to the first piece.
	*/
	public static Piece makeFastRotations(Piece root) {
		Piece origin=root;
		Piece cur=origin;
		Piece piece=null;
		while(true) {
			piece=cur;
			cur.next = cur.computeNextRotation();
			cur=cur.next;
			if(origin.equals(cur))
				break;
		}
		piece.next = origin;
		return origin;
		// YOUR CODE HERE
	}
	
	

	/**
	 Given a string of x,y pairs ("0 0	0 1 0 2 1 0"), parses
	 the points into a TPoint[] array.
	 (Provided code)
	*/
	private static TPoint[] parsePoints(String string) {
		List<TPoint> points = new ArrayList<TPoint>();
		StringTokenizer tok = new StringTokenizer(string);
		try {
			while(tok.hasMoreTokens()) {
				int x = Integer.parseInt(tok.nextToken());
				int y = Integer.parseInt(tok.nextToken());
				
				points.add(new TPoint(x, y));
			}
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse x,y string:" + string);
		}
		
		// Make an array out of the collection
		TPoint[] array = points.toArray(new TPoint[0]);
		return array;
	}

	


}
