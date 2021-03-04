import java.text.DecimalFormat ;
import java.util.Arrays;
public class TableModel {
    SimplexModel model ;
    double table [] [] ;
    double cb [] , cj [] , zj [] , cjMinZj [] , theta [] ;
    String XBRow [] , XBCol [] ;
    int MAX = 10000000 ; 
    int idxPivot , jdxPivot ;
    double pivotElement ;
    DecimalFormat df = new DecimalFormat() ;
    String status = null ;
    int pos ;
    public TableModel ( SimplexModel par ){
        model = par ;
        zj = new double [ model.totalNumOfVar ] ;
        cjMinZj = new double [ model.totalNumOfVar ] ;
        theta = new double [ model.numOfConstrains ] ;
        XBCol = new String [ model.totalNumOfVar ] ;
        cj = new double [ model.totalNumOfVar ] ;
        XBRow = new String [ model.numOfConstrains ] ;
        cb = new double [ model.numOfConstrains ] ;
        table = new double [ model.numOfConstrains ] [ model.totalNumOfVar + 1 ] ;
        df.setMaximumFractionDigits ( 2 ) ;
    }
    public void createBasics (){
        
        int i ;
        for ( i = 0 ; i < model.numOfBasicVar ; ++i ){
            XBCol [ i ] = "X" + ( i + 1 ) ;
            if ( model.max == false )
                cj [ i ] = -model.basicVarValus [ i ] ;
            else    
                cj [ i ] = model.basicVarValus [ i ] ;
        }
        //Slacks = + , sirplus = - , artficial = + ;
        for ( int j = 1 ; j <= model.numOfSlacks ; ++i , ++j ){
            XBCol [ i ] = "Sl" + ( j ) ;
            cj [ i ] = 0 ;
        }
        
        for ( int j = 1 ; j <= model.numOfArtficial ; ++i , ++j ){
            XBCol [ i ] = "A" + ( j ) ;
            cj [ i ] = -MAX ;
        }
        
        for ( int j = 1 ; j <= model.numOfsirplus ; ++i , ++j ){
            XBCol [ i ] = "Sr" + ( j ) ;
            cj [ i ] = 0 ;
        }
        
        int sl = 1 , ar = 1 ;
        for ( i = 0 ; i < model.numOfConstrains ; ++i ){
            if ( model.costrainsSlaks [ i ] == 1 ){
                XBRow [ i ] = "Sl" + ( sl++ ) ;
                cb [ i ] = 0 ;
            }else{
                XBRow [ i ] = "A" + ( ar++ ) ;
                cb [ i ] = -MAX ;
            }
        }
    }
    public void createTable (){
        createBasics() ;
        int jdxSl = model.numOfBasicVar ;
        pos = jdxSl ;
        int jdxAr = model.numOfBasicVar + model.numOfSlacks ;
        int jdxSr = model.numOfBasicVar + model.numOfArtficial + model.numOfSlacks ;
        for ( int i = 0 ; i < model.numOfConstrains ; ++i ){
            int j = 0 ;
            for ( j = 0 ; j < model.numOfBasicVar ; ++j ){
                table [ i ] [ j ] = model.costrainsValus [ i ] [ j ] ;
            }
            if ( model.costrainsSlaks [ i ] == 1 )
                table [ i ] [ jdxSl++ ] = 1 ;
            
            if ( model.costrainsArtficial [ i ] == 1 )
                table [ i ] [ jdxAr++ ] = 1 ;
            
            if ( model.costrainsSirplus [ i ] == -1 )
                table [ i ] [ jdxSr++ ] = -1 ;
            table [ i ] [ model.totalNumOfVar ] = model.BValues [ i ] ;

        }
       
        
    }
    public void displayTable (){
       
        System.out.print ( "\tCJ\t" ) ;
        for ( int i = 0 ; i < cj.length ; ++i )
            System.out.print ( ( cj [ i ] == -MAX ? "-M" : df.format( cj [ i ] ) ) + "\t" ) ;
        System.out.println ( "" ) ;
        System.out.print ("CB\tXB\t");
        for ( int i = 0 ; i < XBCol.length ; ++i )
            System.out.print( XBCol [ i ] + "\t" ) ;
        System.out.println ( "Bi\ttheta" ) ;
        for ( int i = 0 ; i < model.numOfConstrains ; ++i ){
            System.out.print ( ( cb [ i ] == -MAX ? "-M" : df.format( cb [ i ] ) ) + "\t" + XBRow [ i ] + "\t" ) ;
            for ( int j = 0 ; j < table [ i ].length ; ++j )
                System.out.print ( df.format( table [ i ] [ j ] )  + "\t" ) ;
            System.out.println ( ( theta [ i ] == -1 ? "--" : df.format( theta [ i ] ) ) ) ;
        }
        
        calc_Zj() ;
        calc_CjMinZj() ;
        System.out.print ( "\tZJ\t" ) ;
        
        for ( int i = 0 ; i < zj.length ; ++i )
            System.out.print ( df.format( zj [ i ] ) + "\t" ) ;
        System.out.println( "" ) ;
        System.out.print ( "\tCJ-ZJ\t" ) ;
        for ( int i = 0 ; i < cj.length ; ++i )
            System.out.print ( df.format( cjMinZj [ i ] ) + "\t" ) ;
        System.out.println( "" ) ;
        
    }
    public void calc_Zj (){
        for ( int i = 0 ; i < table[ 0 ].length - 1 ; ++i ){
            double sum = 0 ;
            for ( int j = 0 ; j < table .length ; ++j ){
                sum += cb [ j ] * table [ j ] [ i ] ;
            }
            zj [ i ] = sum ;
        }
    }
    public void calc_CjMinZj (){
        for ( int i = 0 ; i < table [ 0 ].length - 1 ; ++i )
            cjMinZj [ i ] = cj [ i ] - zj [ i ] ;
    }
    public boolean checkEnd(){
        for ( int i = 0 ; i < cjMinZj.length ; ++i )
            if ( cjMinZj [ i ] > 0 )
                return false ;
        return true ;
    }
    public void maxCjMinZj (){
        jdxPivot = 0 ;
        for ( int i = 1 ; i < cjMinZj.length ; ++i )
            if ( cjMinZj [ jdxPivot ] < cjMinZj [ i ] )
                jdxPivot = i ;
    }
    public void minTheta (){
        idxPivot = 0 ;
        while ( idxPivot < theta.length && theta [ idxPivot ] == -1 )
            ++idxPivot ;
        if ( idxPivot == theta.length ){
            idxPivot = -1 ;
            return ;
        }
        for ( int i = 0 ; i < theta.length ; ++i ){
            if ( theta [ idxPivot ] > theta [ i ] ){
                if ( theta [ i ] != -1 )
                    idxPivot = i ;
            }
        }
    }
    public void calc_theta(){
        for ( int i = 0 ; i < model.numOfConstrains ; ++i )
            if ( table [ i ] [ jdxPivot ] != 0 && table [ i ] [ model.totalNumOfVar ] / table [ i ] [ jdxPivot ] > 0 )
                theta [ i ] = table [ i ] [ model.totalNumOfVar ] / table [ i ] [ jdxPivot ] ;
            else
                theta [ i ] = -1 ;
        
    }
    public String status ( int spId ){
        if ( spId == 1 )
            return "UnBounded Solution" ;
        else{
            boolean flag = false ;
            for ( int i = 0 ; i < XBRow.length ; ++i )
                if ( XBRow[ i ].charAt( 0 ) == 'A' )
                    flag = true ;
            if ( flag )
                return "Infeasible Solution" ;
            
            int cnt = 0 ;
            for ( int i = 0 ; i < cjMinZj.length ; ++i )
                if ( cjMinZj [ i ] == 0 )
                    ++cnt ;
            if ( cnt > XBRow.length ){
                for ( int i = 0 ; i < cjMinZj.length ; ++i ){
                    boolean found = false ;
                    if ( cjMinZj [ i ] == 0 ){
                        for ( int j = 0 ; j < XBRow.length ; ++j ){
                            if ( XBCol [ i ].equals( XBRow [ j ] ) == true ){
                                found = true ;
                            }
                        }
                        if ( !found )
                            jdxPivot = i ;
                    }
                }
                System.out.println( ValuesAnswer() ) ;
                solveMultiOptimal() ;
                System.out.println( ValuesAnswer() ) ;
                return "Multi Optimal Solution " ;
            }
            System.out.println( ValuesAnswer() );
            return "Optimal Solution" ;
        }
    }
    public String ValuesAnswer() {
        String ret = "( " ;
        double z = 0 ;
        for ( int i = 0 ; i < XBRow.length ; ++i ){
            String x = "X" + ( i + 1 ) ;
            boolean found = false ;
            for ( int j = 0 ; j < XBRow.length ; ++j )
                if ( x.equals( XBRow [ j ] ) ){
                    z += cb [ j ] * table [ j ] [ model.totalNumOfVar ] ;
                    ret += XBRow [ j ] + " = " + df.format( table [ j ] [ model.totalNumOfVar ] ) + " , " ;
                    found = true ;
                }
            if ( !found ){
                ret += x + " = " + 0 + " , " ;
            }
        }
        
        ret = ret + "Z = " + df.format( Math.abs ( z ) ) + " )" ;
        return ret ;
    }
    public void solve (){
        System.out.println("") ;
        displayTable() ;
        System.out.println("") ;
        while ( checkEnd() == false ){
            calc_Zj() ;
            calc_CjMinZj() ;
            maxCjMinZj() ;
            calc_theta() ;
            minTheta() ;
            
            if ( idxPivot == -1 && checkEnd() == false ){
                //special Case 
                status = status ( 1 ) ;
                break ;
            }
            XBRow [ idxPivot ] = XBCol [ jdxPivot ] ;
            cb [ idxPivot ] = cj [ jdxPivot ] ;
            pivotElement = table [ idxPivot ] [ jdxPivot ] ;
            
            int pi = idxPivot , pj = jdxPivot ;
            for ( int ci = 0 ; ci < table.length ; ++ci ){
                for ( int cj = 0 ; cj < table [ 0 ].length ; ++cj ){
                    if ( ci == pi || cj == pj )
                        continue ;
                    table [ ci ] [ cj ] =  ( ( table [ pi ] [ pj ] * table [ ci ] [ cj ] )
                                            - ( table [ ci ] [ pj ] * table [ pi ] [ cj ] ) ) ;
                    table [ ci ] [ cj ] /= pivotElement ;
                }
            }
            
            
            for ( int i = 0 ; i < table [ 0 ].length ; ++i ){
                table [ idxPivot ] [ i ] /= pivotElement ;
            }
            for ( int i = 0 ; i < table.length ; ++i ){
                if ( i == pi )
                    table [ i ] [ pj ] = 1 ;
                else
                    table [ i ] [ pj ] = 0 ;
            }
            
            displayTable() ;
            System.out.println("");
            
        }
        if ( status == null )
            status = status( 0 ) ;
        System.out.println( status ) ;
        //calc_Delta() ;
    }
    public void calc_Delta (){
        for ( int i = 0 ; i < model.numOfConstrains ; ++i ){
            System.out.println( table [ i ] [ model.totalNumOfVar ] + "+" + table [ i ] [ pos ] + ">= 0 " ) ;
        }
        double deltaValues [ ] = new double [ model.numOfConstrains ] ;
        for ( int i = 0 ; i < model.numOfConstrains ; ++i ){
            if ( table [ i ] [ pos ] == 0 ){
                deltaValues [ i ] = -Integer.MAX_VALUE ;
                continue ;
            }
            deltaValues [ i ] = ( -table [ i ] [ model.totalNumOfVar ] ) / table [ i ] [ pos ] ;
            System.out.println( deltaValues [ i ] ) ;
        }
        Arrays.sort( deltaValues ) ;
        double L = -Integer.MAX_VALUE , R = Integer.MAX_VALUE ;
        for ( int i = 0 ; i < deltaValues.length ; ++i ){
            if ( deltaValues [ i ] >= L )
                L = deltaValues [ i ] ;
            else if ( deltaValues [ i ] <= R )
                R = deltaValues [ i ] ;
        }
        System.out.println( L + " <= " + "delta" + " <= " + ( ( R >= Integer.MAX_VALUE ) ? "infinity" : R ) ) ;
        double originalB = -model.BValues [ 0 ] ;
        L += originalB ;
        if ( R != Integer.MAX_VALUE )
            R += originalB ;
        System.out.println( L + " <=" + "delta" + " <= " + ( ( R >= Integer.MAX_VALUE ) ? "infinity" : R ) ) ;
        
    }
    public void solveMultiOptimal (){
        
        calc_theta() ;
        minTheta() ;
        XBRow [ idxPivot ] = XBCol [ jdxPivot ] ;
        cb [ idxPivot ] = cj [ jdxPivot ] ;
        pivotElement = table [ idxPivot ] [ jdxPivot ] ;

        int pi = idxPivot , pj = jdxPivot ;
        for ( int ci = 0 ; ci < table.length ; ++ci ){
            for ( int cj = 0 ; cj < table [ 0 ].length ; ++cj ){
                if ( ci == pi || cj == pj )
                    continue ;
                table [ ci ] [ cj ] =  ( ( table [ pi ] [ pj ] * table [ ci ] [ cj ] )
                                        - ( table [ ci ] [ pj ] * table [ pi ] [ cj ] ) ) ;
                table [ ci ] [ cj ] /= pivotElement ;
            }
        }


        for ( int i = 0 ; i < table [ 0 ].length ; ++i ){
            table [ idxPivot ] [ i ] /= pivotElement ;
        }
        for ( int i = 0 ; i < table.length ; ++i ){
            if ( i == pi )
                table [ i ] [ pj ] = 1 ;
            else
                table [ i ] [ pj ] = 0 ;
        }

        displayTable() ;
        System.out.println("");
    }    
}