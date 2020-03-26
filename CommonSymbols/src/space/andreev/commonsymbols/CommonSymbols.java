package space.andreev.commonsymbols;

public class CommonSymbols {

	public static final char[] engToRuWritten = new char[65536];
	public static final char[] ruToEngWritten = new char[65536];
	
	static {
		engToRuWritten['A'] = '�';
		engToRuWritten['B'] = '�';
		engToRuWritten['C'] = '�';
		engToRuWritten['D'] =  0 ;
		engToRuWritten['E'] = '�';
		engToRuWritten['F'] =  0 ;
		engToRuWritten['G'] =  0 ;
		engToRuWritten['H'] = '�';
		engToRuWritten['I'] =  0 ;
		engToRuWritten['J'] =  0 ;
		engToRuWritten['K'] = '�';
		engToRuWritten['L'] =  0 ;
		engToRuWritten['M'] = '�';
		engToRuWritten['N'] =  0 ;
		engToRuWritten['O'] = '�';
		engToRuWritten['P'] = '�';
		engToRuWritten['Q'] =  0 ;
		engToRuWritten['R'] =  0 ;
		engToRuWritten['S'] =  0 ;
		engToRuWritten['T'] = '�';
		engToRuWritten['U'] =  0 ;
		engToRuWritten['V'] =  0 ;
		engToRuWritten['W'] =  0 ;
		engToRuWritten['X'] = '�';
		engToRuWritten['Y'] =  0 ;
		engToRuWritten['Z'] =  0 ;
		
		engToRuWritten['a'] = '�';
		engToRuWritten['b'] = '�';
		engToRuWritten['c'] = '�';
		engToRuWritten['d'] =  0 ;
		engToRuWritten['e'] = '�';
		engToRuWritten['f'] =  0 ;
		engToRuWritten['g'] =  0 ;
		engToRuWritten['h'] =  0 ;
		engToRuWritten['i'] =  0 ;
		engToRuWritten['j'] =  0 ;
		engToRuWritten['k'] = '�';
		engToRuWritten['l'] =  0 ;
		engToRuWritten['m'] = '�';
		engToRuWritten['n'] =  0 ;
		engToRuWritten['o'] = '�';
		engToRuWritten['p'] = '�';
		engToRuWritten['q'] =  0 ;
		engToRuWritten['r'] = '�';
		engToRuWritten['s'] =  0 ;
		engToRuWritten['t'] =  0 ;
		engToRuWritten['u'] = '�';
		engToRuWritten['v'] =  0 ;
		engToRuWritten['w'] =  0 ;
		engToRuWritten['x'] = '�';
		engToRuWritten['y'] = '�';
		engToRuWritten['z'] =  0 ;
		
		for(char index = 'A'; index < 'Z'; index++) {
			char ruSymbol = engToRuWritten[index];
			ruToEngWritten[ruSymbol] = index;
		}
	}
	
	public static char getEngWritten(char ruSymb) {
		return ('�' <= ruSymb && ruSymb <= '�' || '�' <= ruSymb && ruSymb <= '�') ? ruToEngWritten[ruSymb] : ruSymb;
	}
	
}
