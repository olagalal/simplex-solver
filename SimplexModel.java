import java.util.Scanner ;
public class SimplexModel {
    Scanner in = new Scanner( System.in ) ;
    boolean max ;
    public int numOfBasicVar ;
    public int numOfConstrains ;
    public int numOfSlacks ;
    public int numOfsirplus ;
    public int numOfArtficial ;
    int totalNumOfVar ;
    public double basicVarValus [] ;
    public double costrainsValus [][] ;
    public double BValues [] ;
    public int costrainsSirplus [] ;
    public int costrainsArtficial [] ;
    public int costrainsSlaks [] ;
    public void inputModelData() {
        System.out.print("Enter 1 if Max or 0 if min : " ) ;
        int temp = in.nextInt() ;
        while ( !( temp == 1 || temp == 0 ) ){
            System.out.println ( "Error , Enter Again " ) ;
            System.out.print("Enter 1 if Max or 0 if min : " ) ;
            temp = in.nextInt() ;
        }
        
        max = ( temp == 1 ) ;
        System.out.print ( "Enter The Number Of The Decision Variable : " ) ;
        numOfBasicVar = in.nextInt() ;
        basicVarValus = new double [ numOfBasicVar ] ;
        
        
        for ( int i = 0 ; i < numOfBasicVar ; ++i ){
            System.out.print ( "Enter X" + ( i + 1 ) + " value : " ) ;
            basicVarValus [ i ] = in.nextDouble() ;
        }
        
        
        System.out.print ( "Enter The Number Of Constrains : " ) ;
        numOfConstrains = in.nextInt() ;
        costrainsValus = new double [ numOfConstrains ] [ numOfBasicVar ] ;
        costrainsSlaks = new int [ numOfConstrains ] ;
        costrainsSirplus = new int [ numOfConstrains ] ;
        costrainsArtficial = new int [ numOfConstrains ]  ;
        BValues = new double [ numOfConstrains ] ;
        
        for ( int i = 0 ; i < numOfConstrains ; ++i ){
            System.out.println( "constrain " + ( i + 1 ) + " detail :" ) ;
            for ( int j = 0 ; j < numOfBasicVar ; ++j ){
                System.out.print ( "Enter X" + ( j + 1 ) + " Value : " ) ;
                costrainsValus [ i ] [ j ] = in.nextDouble() ;
            }
            System.out.print ( "Enter 1 if <= or 2 if >= or 3 if = : " ) ;
            temp = in.nextInt() ;
            while ( !( temp == 1 || temp == 2 || temp == 3 ) ){
                System.out.print ( "Error , Enter 1 if < or 2 if > or 3 if = : " ) ;
                temp = in.nextInt() ;
            }
            if ( temp == 1 ){
                ++numOfSlacks ;
                costrainsSlaks [ i ] = 1 ;
            }
            else if ( temp == 2 ){
                ++numOfsirplus ;
                costrainsSirplus [ i ] = -1 ;
                ++numOfArtficial ;
                costrainsArtficial [ i ] = 1 ;
            }else {
                ++numOfArtficial ;
                costrainsArtficial [ i ] = 1 ;
            }
            System.out.print( "Enter the Value if Bi : " ) ;
            BValues [ i ] = in.nextDouble() ;
        }
        totalNumOfVar = numOfBasicVar + numOfSlacks + numOfsirplus + numOfArtficial ;
    }
    
}