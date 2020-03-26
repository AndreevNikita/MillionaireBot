package space.andreev.commonsymbols;

public class CommonSymbols {

	public static final char[] engToRuWritten = new char[65536];
	public static final char[] ruToEngWritten = new char[65536];
	
	static {
		engToRuWritten['A'] = 'À';
		engToRuWritten['B'] = 'Â';
		engToRuWritten['C'] = 'Ñ';
		engToRuWritten['D'] =  0 ;
		engToRuWritten['E'] = 'Å';
		engToRuWritten['F'] =  0 ;
		engToRuWritten['G'] =  0 ;
		engToRuWritten['H'] = 'Í';
		engToRuWritten['I'] =  0 ;
		engToRuWritten['J'] =  0 ;
		engToRuWritten['K'] = 'Ê';
		engToRuWritten['L'] =  0 ;
		engToRuWritten['M'] = 'Ì';
		engToRuWritten['N'] =  0 ;
		engToRuWritten['O'] = 'Î';
		engToRuWritten['P'] = 'Ð';
		engToRuWritten['Q'] =  0 ;
		engToRuWritten['R'] =  0 ;
		engToRuWritten['S'] =  0 ;
		engToRuWritten['T'] = 'Ò';
		engToRuWritten['U'] =  0 ;
		engToRuWritten['V'] =  0 ;
		engToRuWritten['W'] =  0 ;
		engToRuWritten['X'] = 'Õ';
		engToRuWritten['Y'] =  0 ;
		engToRuWritten['Z'] =  0 ;
		
		engToRuWritten['a'] = 'à';
		engToRuWritten['b'] = 'â';
		engToRuWritten['c'] = 'ñ';
		engToRuWritten['d'] =  0 ;
		engToRuWritten['e'] = 'å';
		engToRuWritten['f'] =  0 ;
		engToRuWritten['g'] =  0 ;
		engToRuWritten['h'] =  0 ;
		engToRuWritten['i'] =  0 ;
		engToRuWritten['j'] =  0 ;
		engToRuWritten['k'] = 'ê';
		engToRuWritten['l'] =  0 ;
		engToRuWritten['m'] = 'ì';
		engToRuWritten['n'] =  0 ;
		engToRuWritten['o'] = 'î';
		engToRuWritten['p'] = 'ð';
		engToRuWritten['q'] =  0 ;
		engToRuWritten['r'] = 'ã';
		engToRuWritten['s'] =  0 ;
		engToRuWritten['t'] =  0 ;
		engToRuWritten['u'] = 'è';
		engToRuWritten['v'] =  0 ;
		engToRuWritten['w'] =  0 ;
		engToRuWritten['x'] = 'õ';
		engToRuWritten['y'] = 'ó';
		engToRuWritten['z'] =  0 ;
		
		for(char index = 'A'; index < 'Z'; index++) {
			char ruSymbol = engToRuWritten[index];
			ruToEngWritten[ruSymbol] = index;
		}
	}
	
	public static char getEngWritten(char ruSymb) {
		return ('à' <= ruSymb && ruSymb <= 'ÿ' || 'À' <= ruSymb && ruSymb <= 'ß') ? ruToEngWritten[ruSymb] : ruSymb;
	}
	
}
