

/* This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */ 


/*  
 author: kurmiashish
 */
package freenet.clients.http.filter;
import java.io.*;
import java.util.*;
import java.lang.StringBuffer;
import freenet.support.Logger;

class CSSTokenizerFilter {
	private Reader r;
	Writer w = null;
	FilterCallback cb;
	boolean debug;

	CSSTokenizerFilter(){}


	public void log(String s)
	{
		Logger.debug(this,s);
		//System.out.println("CSSTokenizerFilter: "+s);
	}
	CSSTokenizerFilter(Reader r, Writer w, FilterCallback cb,boolean debug) {
		this.r=r;
		this.w = w;
		this.cb=cb;
		CSSPropertyVerifier.cb=cb;
		this.debug=debug;
		CSSPropertyVerifier.debug=debug;
	}

	public void parse() throws IOException
	{
		try
		{
			filterCSS();
		}
		catch(IOException e)
		{
			throw e;
		}
	}
	public boolean isValidURI(String URI)
	{
		try
		{
			return URI.equals(cb.processURI(URI, null));
		}
		catch(CommentException e)
		{
			return false;
		}
	}


	//Function to merge two arrays into third array.
	public static <T> T[] concat(T[] a, T[] b) {
		final int alen = a.length;
		final int blen = b.length;
		if (alen == 0) {
			return b;
		}
		if (blen == 0) {
			return a;
		}
		final T[] result = (T[]) java.lang.reflect.Array.
		newInstance(a.getClass().getComponentType(), alen + blen);
		System.arraycopy(a, 0, result, 0, alen);
		System.arraycopy(b, 0, result, alen, blen);
		return result;
	}


	/* To save the memory, only those Verifier objects would be created which are actually present in the CSS document.
	 * allelementVerifiers contains all the CSS property tags as String. All loaded Verifier objects are stored in elementVerifier.
	 * When retrieving a Verifier object, first it is searched in elementVerifiers to see if it is already loaded.
	 * If it is not loaded then allelementVerifiers is checked to see if the property name is valid. If it is valid, then the desired Verifier object is loaded in allelemntVerifiers.
	 */
	static Map<String, CSSPropertyVerifier> elementVerifiers = new HashMap<String, CSSPropertyVerifier>();
	static HashSet<String> allelementVerifiers=new HashSet<String>(); 
	//Reference http://www.w3.org/TR/CSS2/propidx.html
	static {
		allelementVerifiers.add("@media");
		allelementVerifiers.add("azimuth");
		allelementVerifiers.add("background-attachment");
		allelementVerifiers.add("background-color");
		allelementVerifiers.add("background-image");
		allelementVerifiers.add("background-position");
		allelementVerifiers.add("background-repeat");
		allelementVerifiers.add("background");
		allelementVerifiers.add("border-collapse");
		allelementVerifiers.add("border-color");
		allelementVerifiers.add("border-spacing");
		allelementVerifiers.add("border-style");
		allelementVerifiers.add("border-left");
		allelementVerifiers.add("border-top");
		allelementVerifiers.add("border-right");
		allelementVerifiers.add("border-bottom");
		allelementVerifiers.add("border-top-color");
		allelementVerifiers.add("border-right-color");
		allelementVerifiers.add("border-bottom-color");
		allelementVerifiers.add("border-left-color");
		allelementVerifiers.add("border-top-style");
		allelementVerifiers.add("border-right-style");
		allelementVerifiers.add("border-bottom-style");
		allelementVerifiers.add("border-top-width");
		allelementVerifiers.add("border-right-width");
		allelementVerifiers.add("border-bottom-width");
		allelementVerifiers.add("border-left-width");
		allelementVerifiers.add("border-width");
		allelementVerifiers.add("border");
		allelementVerifiers.add("bottom");
		allelementVerifiers.add("caption-side");
		allelementVerifiers.add("clear");
		allelementVerifiers.add("clip");
		allelementVerifiers.add("color");
		allelementVerifiers.add("content");
		allelementVerifiers.add("counter-increment");
		allelementVerifiers.add("counter-reset");
		allelementVerifiers.add("cue-after");
		allelementVerifiers.add("cue-before");
		allelementVerifiers.add("cue");
		allelementVerifiers.add("cursor");
		allelementVerifiers.add("direction");
		allelementVerifiers.add("display");
		allelementVerifiers.add("elevation");
		allelementVerifiers.add("empty-cells");
		allelementVerifiers.add("float");
		allelementVerifiers.add("font-family");
		allelementVerifiers.add("font-size");
		allelementVerifiers.add("font-style");
		allelementVerifiers.add("font-variant");
		allelementVerifiers.add("font-weight");
		allelementVerifiers.add("font");
		allelementVerifiers.add("height");
		allelementVerifiers.add("left");
		allelementVerifiers.add("letter-spacing");
		allelementVerifiers.add("line-height");
		allelementVerifiers.add("list-style-image");
		allelementVerifiers.add("list-style-position");
		allelementVerifiers.add("list-style-type");
		allelementVerifiers.add("list-style");
		allelementVerifiers.add("margin-right");
		allelementVerifiers.add("margin-left");
		allelementVerifiers.add("margin-top");
		allelementVerifiers.add("margin-bottom");
		allelementVerifiers.add("margin");
		allelementVerifiers.add("max-height");
		allelementVerifiers.add("max-width");
		allelementVerifiers.add("min-height");
		allelementVerifiers.add("min-width");
		allelementVerifiers.add("orphans");
		allelementVerifiers.add("outline-color");
		allelementVerifiers.add("outline-style");
		allelementVerifiers.add("outline-width");
		allelementVerifiers.add("outline");
		allelementVerifiers.add("overflow");
		allelementVerifiers.add("padding-top");
		allelementVerifiers.add("padding-right");
		allelementVerifiers.add("padding-bottom");
		allelementVerifiers.add("padding-left");
		allelementVerifiers.add("padding");
		allelementVerifiers.add("page-break-after");
		allelementVerifiers.add("page-break-before");
		allelementVerifiers.add("page-break-inside");
		allelementVerifiers.add("pause-after");
		allelementVerifiers.add("pause-before");
		allelementVerifiers.add("pause");
		allelementVerifiers.add("pitch-range");
		allelementVerifiers.add("pitch");
		allelementVerifiers.add("play-during");
		allelementVerifiers.add("position");
		allelementVerifiers.add("quotes");
		allelementVerifiers.add("richness");
		allelementVerifiers.add("right");
		allelementVerifiers.add("speak-header");
		allelementVerifiers.add("speak-numeral");
		allelementVerifiers.add("speak-punctuation");
		allelementVerifiers.add("speak");
		allelementVerifiers.add("speech-rate");
		allelementVerifiers.add("stress");
		allelementVerifiers.add("table-layout");
		allelementVerifiers.add("text-align");
		allelementVerifiers.add("text-decoration");
		allelementVerifiers.add("text-indent");
		allelementVerifiers.add("text-transform");
		allelementVerifiers.add("top");
		allelementVerifiers.add("unicode-bidi");
		allelementVerifiers.add("vertical-align");
		allelementVerifiers.add("visibility");
		allelementVerifiers.add("voice-family");
		allelementVerifiers.add("volume");
		allelementVerifiers.add("white-space");
		allelementVerifiers.add("widows");
		allelementVerifiers.add("width");
		allelementVerifiers.add("word-spacing");
		allelementVerifiers.add("z-index");


	}

	/*
	 * Array for storing additional Verifier objects for validating Regular expressions in CSS Property value
	 * e.g. [ <color> | transparent]{1,4}. It is explained in detail in CSSPropertyVerifier class
	 */
	static CSSPropertyVerifier[] auxilaryVerifiers=new CSSPropertyVerifier[100];
	static
	{
		/*CSSPropertyVerifier(String[] allowedValues,String[] possibleValues,String expression,boolean onlyValueVerifier)*/
		//for background-position
		auxilaryVerifiers[2]=new CSSPropertyVerifier(new String[]{"left","center","right"},new String[]{"pe","le"},null,true);
		auxilaryVerifiers[3]=new CSSPropertyVerifier(new String[]{"top","center","bottom"},new String[]{"pe","le"},null,true);
		auxilaryVerifiers[4]=new CSSPropertyVerifier(new String[]{"left","center","right"},null,null,true);
		auxilaryVerifiers[5]=new CSSPropertyVerifier(new String[]{"top","center","bottom"},null,null,true);
		//<border-style>
		auxilaryVerifiers[13]=new CSSPropertyVerifier(new String[]{"none","hidden","dotted","dashed","solid","double","groove","ridge","inset","outset", "inherit"},new String[]{"le"},null,true);
		//<border-width>
		auxilaryVerifiers[14]=new CSSPropertyVerifier(new String[]{"thin","medium","thick"},new String[]{"le"},null,true);
		//<border-top-color>
		auxilaryVerifiers[15]=new CSSPropertyVerifier(new String[] {"transparent","inherit"},new String[]{"co"},null,true);
	}
	/* This function loads a verifier object in elementVerifiers.
	 * After the object has been loaded, property name is removed from allelementVerifier.
	 */
	private static void addVerifier(String element)
	{
		if("@media".equalsIgnoreCase(element))
		{
			elementVerifiers.put(element,new CSSPropertyVerifier(null,new String[] {"all","braille","embossed","handheld","print","projection","screen","speech","tty","tv"},null,null,null));
			allelementVerifiers.remove(element);
		}
		else if("azimuth".equalsIgnoreCase(element))
		{      
			auxilaryVerifiers[0]=new CSSPropertyVerifier(new String[]{"left-side","far-left","left","center-left","center","center-right","right","far-right","right-side"},null,null,true);
			auxilaryVerifiers[1]=new CSSPropertyVerifier(new String[]{"behind"},null,null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"leftwards","rightwards","inherit"},ElementInfo.AURALMEDIA,new String[]{"an"},new String[]{"0a1"}));
			allelementVerifiers.remove(element);
		}
		else if("background-attachment".equalsIgnoreCase(element)){ 
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"scroll","fixed","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("background-color".equalsIgnoreCase(element)){
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"transparent","inherit"},ElementInfo.VISUALMEDIA,new String[]{"co"}));
			allelementVerifiers.remove(element);

		}
		else if("background-image".equalsIgnoreCase(element)){
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"no","inherit"},ElementInfo.VISUALMEDIA,new String[]{"ur"}));
			allelementVerifiers.remove(element);
		}
		else if("background-position".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"2 3?","4a5"}));
			allelementVerifiers.remove(element);
		}
		else if("background-repeat".equalsIgnoreCase(element))
		{
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"repeat","repeat-x","repeat-y","no-repeat","inherit"},ElementInfo.VISUALMEDIA,null));
			allelementVerifiers.remove(element);
		}
		else if("background".equalsIgnoreCase(element))
		{      

			//background-attachment
			auxilaryVerifiers[6]=new CSSPropertyVerifier(new String[] {"scroll","fixed","inherit"},null,null,true);
			//background-color
			auxilaryVerifiers[7]=new CSSPropertyVerifier(new String[] {"trasparent","inherit"},new String[]{"co"},null,true);
			//background-image
			auxilaryVerifiers[8]=new CSSPropertyVerifier(new String[] {"no","inherit"},new String[]{"ur"},null,true);
			//background-position
			auxilaryVerifiers[9]=new CSSPropertyVerifier(new String[] {"inherit"},null,new String[]{"2 3?","4a5"},true);
			//background-repeat
			auxilaryVerifiers[10]=new CSSPropertyVerifier(new String[] {"repeat","repeat-x","repeat-y","no-repeat","inherit"},null,null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"6a7a8a9a10"}));
			allelementVerifiers.remove(element);
		}

		else if("border-collapse".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.TABLEELEMENTS,new String[] {"collapse","separate","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);

		}
		else if("border-color".equalsIgnoreCase(element))
		{      
			auxilaryVerifiers[11]=new CSSPropertyVerifier(new String[] {"transparent"},new String[]{"co"},null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,new String[]{"co"},new String[]{"11<1,4>"}));
			allelementVerifiers.remove(element);

		}
		else if("border-spacing".equalsIgnoreCase(element))
		{      
			auxilaryVerifiers[12]=new CSSPropertyVerifier(null,new String[]{"le"},null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.TABLEELEMENTS,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"12 12?"}));
			allelementVerifiers.remove(element);
		}
		else if("border-style".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"13<1,4>"}));
			allelementVerifiers.remove(element);
		}
		else if("border-left".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"13a14a15"}));
			allelementVerifiers.remove(element);
		}
		else if("border-top".equalsIgnoreCase(element))
		{
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"13a14a15"}));
			allelementVerifiers.remove(element);
		}
		else if("border-right".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"13a14a15"}));
			allelementVerifiers.remove(element);
		}
		else if("border-bottom".equalsIgnoreCase(element))
		{
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"13a14a15"}));
			allelementVerifiers.remove(element);
		}
		else if("border-top-color".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"transparent","inherit"},ElementInfo.VISUALMEDIA,new String[]{"co"}));
			allelementVerifiers.remove(element);

		}
		else if("border-right-color".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"transparent","inherit"},ElementInfo.VISUALMEDIA,new String[]{"co"}));
			allelementVerifiers.remove(element);
		}
		else if("border-bottom-color".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"transparent","inherit"},ElementInfo.VISUALMEDIA,new String[]{"co"}));
			allelementVerifiers.remove(element);
		}
		else if("border-left-color".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"transparent","inherit"},ElementInfo.VISUALMEDIA,new String[]{"co"}));
			allelementVerifiers.remove(element);
		}
		else if("border-top-style".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none","hidden","dotted","dashed","solid","double","groove","ridge","inset","outset", "inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("border-right-style".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none","hidden","dotted","dashed","solid","double","groove","ridge","inset","outset", "inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("border-bottom-style".equalsIgnoreCase(element))
		{
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none","hidden","dotted","dashed","solid","double","groove","ridge","inset","outset", "inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("border-left-style".equalsIgnoreCase(element))
		{
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none","hidden","dotted","dashed","solid","double","groove","ridge","inset","outset", "inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("border-top-width".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"thin","medium","thick","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le"}));
			allelementVerifiers.remove(element);
		}
		else if("border-right-width".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"thin","medium","thick","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le"}));
			allelementVerifiers.remove(element);
		}
		else if("border-bottom-width".equalsIgnoreCase(element))
		{
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"thin","medium","thick","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le"}));
			allelementVerifiers.remove(element);
		}
		else if("border-left-width".equalsIgnoreCase(element))
		{
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"thin","medium","thick","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le"}));
			allelementVerifiers.remove(element);
		}
		else if("border-width".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"14<1,4>"}));
			allelementVerifiers.remove(element);
		}
		else if("border".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"13a14a15"}));
			allelementVerifiers.remove(element);
		}
		else if("bottom".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);

		}
		else if("caption-side".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(new String[]{"caption"},new String[] {"top","bottom","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);

		}
		else if("clear".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.BLOCKLEVELELEMENTS,new String[] {"none","left","right","both","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("clip".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"sh"}));
			allelementVerifiers.remove(element);
		}
		else if("color".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,new String[]{"co"}));
			allelementVerifiers.remove(element);

		}
		else if("content".equalsIgnoreCase(element))
		{      
			auxilaryVerifiers[16]=new contentPropertyVerifier(new String[]{"open-quote","close-quote","no-open-quote", "no-close-quote" });
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"normal","none","inherit"},ElementInfo.MEDIAARRAY,null,new String[]{"16<1,"+ ElementInfo.UPPERLIMIT+">"}));
			allelementVerifiers.remove(element);
		}
		else if("counter-increment".equalsIgnoreCase(element))
		{      
			auxilaryVerifiers[17]=new CSSPropertyVerifier(null,new String[]{"id"},null,true);
			auxilaryVerifiers[18]=new CSSPropertyVerifier(null,new String[]{"in"},null,true);
			auxilaryVerifiers[19]=new CSSPropertyVerifier(null,null,new String[]{"17 18?"},true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none","inherit"},ElementInfo.MEDIAARRAY,null,new String[]{"19<1,"+ElementInfo.UPPERLIMIT+">[1,2]"}));
			allelementVerifiers.remove(element);
		}
		else if("counter-reset".equalsIgnoreCase(element))
		{     
			auxilaryVerifiers[20]=new CSSPropertyVerifier(null,new String[]{"id"},null,true);
			auxilaryVerifiers[21]=new CSSPropertyVerifier(null,new String[]{"in"},null,true);
			auxilaryVerifiers[22]=new CSSPropertyVerifier(null,null,new String[]{"20 21?"},true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none","inherit"},ElementInfo.MEDIAARRAY,null,new String[]{"22<1,"+ElementInfo.UPPERLIMIT+">[1,2]"}));
			allelementVerifiers.remove(element);
		}
		else if("cue-after".equalsIgnoreCase(element))
		{
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none","inherit"},ElementInfo.AURALMEDIA,new String[]{"ur"}));
			allelementVerifiers.remove(element);

		}
		else if("cue-before".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none","inherit"},ElementInfo.AURALMEDIA,new String[]{"ur"}));
			allelementVerifiers.remove(element);
		}
		else if("cue".equalsIgnoreCase(element))
		{      
			//cue-before
			auxilaryVerifiers[23]=new CSSPropertyVerifier(new String[] {"none","inherit"},new String[]{"ur"},null,true);
			//cue-after
			auxilaryVerifiers[24]=new CSSPropertyVerifier(new String[] {"none","inherit"},new String[]{"ur"},null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.MEDIAARRAY,null,new String[]{"23a24"}));
			allelementVerifiers.remove(element);

		}
		else if("cursor".equalsIgnoreCase(element))
		{      
			auxilaryVerifiers[25]=new CSSPropertyVerifier(null,new String[]{"ur"},null,true);
			auxilaryVerifiers[26]=new CSSPropertyVerifier(new String[]{"auto","crosshair","default","pointer","move","e-resize","ne-resize","nw-resize","n-resize","se-resize","sw-resize","s-resize","w-resize","text","wait","help","progress"},null,null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALINTERACTIVEMEDIA,null,new String[]{"25<0,"+ElementInfo.UPPERLIMIT+"> 26"}));

			allelementVerifiers.remove(element);
		}
		else if("direction".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"ltr","rtl","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("display".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inline","block","list-item","run-in","inline-block","table","inline-table","table-row-group","table-header-group","table-footer-group","table-row","table-column-group","table-column","table-cell","table-caption","none","inherit"},ElementInfo.MEDIAARRAY));
			allelementVerifiers.remove(element);
		}
		else if("elevation".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"below","level","above","higher","lower", "inherit"},ElementInfo.AURALMEDIA,new String[]{"an"}));
			allelementVerifiers.remove(element);
		}
		else if("empty-cells".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(new String[]{"th","td"},new String[] {"show","hide","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("float".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"left","right","none","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("font-family".equalsIgnoreCase(element))
		{      

			auxilaryVerifiers[26]=new CSSPropertyVerifier(ElementInfo.FONT_LIST,null,null,true);
			auxilaryVerifiers[57]=new CSSPropertyVerifier(new String[] {","},null,null,true);
			auxilaryVerifiers[58]=new CSSPropertyVerifier(null,null,new String[]{"57 26"},true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[]{"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"26 58<0,"+ElementInfo.UPPERLIMIT+">[2,2]"}));
			allelementVerifiers.remove(element);
		}
		else if("font-size".equalsIgnoreCase(element))
		{
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"xx-small","x-small","small","medium","large","x-large","xx-large","larger","smaller","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("font-style".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"normal","italic","oblique","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("font-variant".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"normal","small-caps","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("font-weight".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"normal","bold","bolder","lighter","100","200","300","400","500","600","700","800","900","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("font".equalsIgnoreCase(element))
		{      

			//font-style
			auxilaryVerifiers[27]=new CSSPropertyVerifier(new String[] {"normal","italic","oblique","inherit"},null,null,true);
			//font-variant
			auxilaryVerifiers[28]=new CSSPropertyVerifier(new String[] {"normal","small-caps","inherit"},null,null,true);
			//font-weight
			auxilaryVerifiers[29]=new CSSPropertyVerifier(new String[] {"normal","bold","bolder","lighter","100","200","300","400","500","600","700","800","900","inherit"},null,null,true);
			//30-32
			auxilaryVerifiers[30]=new CSSPropertyVerifier(null,null,new String[]{"27a28a29"},true);
			/*
			//font-size
			auxilaryVerifiers[31]=new CSSPropertyVerifier(new String[] {"xx-small","x-small","small","medium","large","x-large","xx-large","larger","smaller","inherit"},null,null,true);
			//line-height
			auxilaryVerifiers[32]=new CSSPropertyVerifier(new String[] {"normal","inherit"},new String[]{"le","pe","re","in"},null,true);

			auxilaryVerifiers[55]=new CSSPropertyVerifier(new String[] {"/"},null,null,true);
			auxilaryVerifiers[56]=new CSSPropertyVerifier(null,null,new String[]{"55 32"},true);
			 */
			auxilaryVerifiers[31]=new FontPartPropertyVerifier();
			//font-family
			auxilaryVerifiers[26]=new CSSPropertyVerifier(ElementInfo.FONT_LIST,null,null,true);
			auxilaryVerifiers[57]=new CSSPropertyVerifier(new String[] {","},null,null,true);
			auxilaryVerifiers[58]=new CSSPropertyVerifier(null,null,new String[]{"57 26"},true);
			auxilaryVerifiers[59]=new CSSPropertyVerifier(new String[]{"inherit"},null,new String[]{"26 58<0,"+ElementInfo.UPPERLIMIT+">[2,2]"},true);


			/*
			 * old font family
			auxilaryVerifiers[53]=new CSSPropertyVerifier(ElementInfo.FONT_LIST,null,null,true);
			auxilaryVerifiers[54]=new CSSPropertyVerifier(new String[]{"inherit"},null,new String[]{"53 53<0,"+ElementInfo.UPPERLIMIT+">"},true);
			 */
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"caption","icon","menu","message-box","small-caption","status-bar","inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"30<0,1>[1,3] 31<1,1>[1,3] 59<1,1>[1,"+ElementInfo.UPPERLIMIT+"]"}));
			//elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"caption","icon","menu","message-box","small-caption","status-bar","inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"31<1,1>[1,3]"}));
			allelementVerifiers.remove(element);
		}
		else if("height".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("left".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("letter-spacing".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"normal","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le"}));
			allelementVerifiers.remove(element);
		}
		else if("line-height".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"normal","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe","re","in"}));
			allelementVerifiers.remove(element);
		}
		else if("list-style-image".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none","inherit"},ElementInfo.VISUALMEDIA,new String[]{"ur"}));
			allelementVerifiers.remove(element);
		}
		else if("list-style-position".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inside","outside","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("list-style-type".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"disc","circle","square","decimal","decimal-leading-zero","lower-roman","upper-roman","lower-greek","lower-latin","upper-latin","armenian","georgian","lower-alpha","upper-alpha","none","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("list-style".equalsIgnoreCase(element))
		{      
			//list-style-image
			auxilaryVerifiers[33]=new CSSPropertyVerifier(new String[] {"none","inherit"},new String[]{"ur"},null,true);
			//list-style-position
			auxilaryVerifiers[34]=new CSSPropertyVerifier(new String[] {"inside","outside","inherit"},null,null,true);
			//list-style-type
			auxilaryVerifiers[35]=new CSSPropertyVerifier(new String[] {"disc","circle","square","decimal","decimal-leading-zero","lower-roman","upper-roman","lower-greek","lower-latin","upper-latin","armenian","georgian","lower-alpha","upper-alpha","none","inherit"},null,null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"33a34a35"}));
			allelementVerifiers.remove(element);
		}
		else if("margin-right".equalsIgnoreCase(element))
		{      
			//margin-width=Length|Percentage|Auto
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("margin-left".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("margin-top".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("margin-bottom".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("margin".equalsIgnoreCase(element))
		{      
			//margin-width
			auxilaryVerifiers[36]=new CSSPropertyVerifier(new String[] {"auto","inherit"},new String[]{"le","pe"},null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"36<1,4>"}));
			allelementVerifiers.remove(element);
		}
		else if("max-height".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.ALLBUTNONREPLACEDINLINEELEMENTS,new String[] {"none","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("max-width".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.ALLBUTNONREPLACEDINLINEELEMENTS,new String[] {"none","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("min-height".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.ALLBUTNONREPLACEDINLINEELEMENTS,new String[] {"inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("min-width".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.ALLBUTNONREPLACEDINLINEELEMENTS,new String[] {"inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("orphans".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.BLOCKLEVELELEMENTS,new String[] {"inherit"},ElementInfo.VISUALPAGEDMEDIA,new String[]{"in"}));
			allelementVerifiers.remove(element);
		}
		else if("outline-color".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"invert", "inherit"},ElementInfo.VISUALINTERACTIVEMEDIA,new String[]{"co"}));
			allelementVerifiers.remove(element);
		}
		else if("outline-style".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none","hidden","dotted","dashed","solid","double","groove","ridge","inset","outset", "inherit"},ElementInfo.VISUALINTERACTIVEMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("outline-width".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"thin","medium","thick","inherit"},ElementInfo.VISUALINTERACTIVEMEDIA,new String[]{"le"}));
			allelementVerifiers.remove(element);
		}
		else if("outline".equalsIgnoreCase(element))
		{      
			//outline-color
			auxilaryVerifiers[37]=new CSSPropertyVerifier(new String[] {"invert", "inherit"},new String[]{"co"},null,true);
			//outline-style
			auxilaryVerifiers[38]=new CSSPropertyVerifier(new String[] {"none","hidden","dotted","dashed","solid","double","groove","ridge","inset","outset", "inherit"},null,null,true);
			//outline-width
			auxilaryVerifiers[39]=new CSSPropertyVerifier(new String[] {"thin","medium","thick","inherit"},new String[]{"le"},null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[]{"inherit"},ElementInfo.VISUALINTERACTIVEMEDIA,new String[]{"le"},new String[]{"37a38a39"}));
			allelementVerifiers.remove(element);
		}
		else if("overflow".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"visible","hidden","scroll","auto","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("padding-top".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.ELEMENTSFORPADDING,new String[] {"inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);

		}
		else if("padding-right".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.ELEMENTSFORPADDING,new String[] {"inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("padding-bottom".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.ELEMENTSFORPADDING,new String[] {"inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("padding-left".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.ELEMENTSFORPADDING,new String[] {"inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("padding".equalsIgnoreCase(element))
		{      
			//padding-width
			auxilaryVerifiers[40]=new CSSPropertyVerifier(new String[] {"inherit"},new String[]{"le","pe"},null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.ELEMENTSFORPADDING,new String[] {"inherit"},ElementInfo.VISUALMEDIA,null,new String[]{"40<1,4>"}));
			allelementVerifiers.remove(element);
		}
		else if("page-break-after".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.BLOCKLEVELELEMENTS,new String[] {"auto","always","avoid","left","right","inherit"},ElementInfo.VISUALPAGEDMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("page-break-before".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.BLOCKLEVELELEMENTS,new String[] {"auto","always","avoid","left","right","inherit"},ElementInfo.VISUALPAGEDMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("page-break-inside".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.BLOCKLEVELELEMENTS,new String[] {"auto","avoid","inherit"},ElementInfo.VISUALPAGEDMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("pause-after".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.AURALMEDIA,new String[]{"ti","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("pause-before".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.AURALMEDIA,new String[]{"ti","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("pause".equalsIgnoreCase(element))
		{      
			auxilaryVerifiers[41]=new CSSPropertyVerifier(new String[] {"inherit"},new String[]{"ti","pe"},null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},null,null,new String[]{"41<1,2>"}));
			allelementVerifiers.remove(element);

		}
		else if("pitch-range".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.AURALMEDIA,new String[]{"in","re"}));
			allelementVerifiers.remove(element);
		}
		else if("pitch".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"x-low","low","medium","high","x-high","inherit"},ElementInfo.AURALMEDIA,new String[]{"fr"}));
			allelementVerifiers.remove(element);

		}
		else if("play-during".equalsIgnoreCase(element))
		{      

			auxilaryVerifiers[42]=new CSSPropertyVerifier(null,new String[]{"ur"},null,true);
			auxilaryVerifiers[43]=new CSSPropertyVerifier(new String[]{"mix"},null,null,true);
			auxilaryVerifiers[44]=new CSSPropertyVerifier(new String[]{"repeat"},null,null,true);
			auxilaryVerifiers[45]=new CSSPropertyVerifier(null,null,new String[]{"42a43"},true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","none","inherit"},ElementInfo.AURALMEDIA,null,new String[]{"44 45<0,1>[1,2]"}));
			allelementVerifiers.remove(element);


		}
		else if("position".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"static","relative","absolute","fixed","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("quotes".equalsIgnoreCase(element))
		{      
			auxilaryVerifiers[46]=new CSSPropertyVerifier(null,new String[]{"st"},null,true);
			auxilaryVerifiers[47]=new CSSPropertyVerifier(null,null,new String[]{"46 46"},true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none","inherit"},null,ElementInfo.VISUALMEDIA,new String[]{"47<1,"+ ElementInfo.UPPERLIMIT+">"}));
			allelementVerifiers.remove(element);
		}
		else if("richness".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.AURALMEDIA,new String[]{"re","in"}));
			allelementVerifiers.remove(element);
		}
		else if("right".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("speak-header".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(new String[]{"th","td"},new String[] {"once","always","inherit"},ElementInfo.AURALMEDIA));
			allelementVerifiers.remove(element);

		}
		else if("speak-numeral".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"digits","continuous","inherit"},ElementInfo.AURALMEDIA));
			allelementVerifiers.remove(element);

		}
		else if("speak-punctuation".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"code", "none","inherit"},ElementInfo.AURALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("speak".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"normal","none","spell-out","inherit"},ElementInfo.AURALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("speech-rate".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"x-slow","slow","medium","fast","x-fast","faster","slower","inherit"},ElementInfo.AURALMEDIA,new String[]{"re","in"}));
			allelementVerifiers.remove(element);
		}
		else if("stress".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},ElementInfo.AURALMEDIA,new String[]{"re","in"}));
			allelementVerifiers.remove(element);
		}
		else if("table-layout".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier( concat(ElementInfo.INLINEELEMENTS,ElementInfo.TABLEELEMENTS),new String[] {"auto","fixed","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("text-align".equalsIgnoreCase(element))
		{
			elementVerifiers.put(element,new CSSPropertyVerifier( concat(concat(ElementInfo.INLINEELEMENTS,ElementInfo.TABLEELEMENTS),ElementInfo.BLOCKLEVELELEMENTS),new String[] {"left","right", "center", "justify" ,"inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("text-decoration".equalsIgnoreCase(element))
		{      
			auxilaryVerifiers[48]=new CSSPropertyVerifier(new String[]{"underline"},null,null,true);
			auxilaryVerifiers[49]=new CSSPropertyVerifier(new String[]{"overline"},null,null,true);
			auxilaryVerifiers[50]=new CSSPropertyVerifier(new String[]{"line-through"},null,null,true);
			auxilaryVerifiers[51]=new CSSPropertyVerifier(new String[]{"blink"},null,null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"none" ,"inherit"},null,ElementInfo.VISUALMEDIA,new String[]{"48a49a50a51"}));
			allelementVerifiers.remove(element);

		}
		else if("text-indent".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier( concat(concat(ElementInfo.INLINEELEMENTS,ElementInfo.TABLEELEMENTS),ElementInfo.BLOCKLEVELELEMENTS),new String[] {"inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("text-transform".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier( ElementInfo.HTMLELEMENTSARRAY,new String[] {"capitalize","uppercase","lowercase","none","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("top".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier( ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("unicode-bidi".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier( ElementInfo.HTMLELEMENTSARRAY,new String[] {"normal", "embed", "bidi-override","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("vertical-align".equalsIgnoreCase(element))
		{      
			//TODO: inline-level and 'table-cell' elements 
			elementVerifiers.put(element,new CSSPropertyVerifier(concat(ElementInfo.INLINEELEMENTS,new String[]{"th","td"}),new String[] {"baseline","sub","super","top","text-top","middle","bottom","text-bottom","inherit"},ElementInfo.VISUALMEDIA,new String[]{"pe","le"}));
			allelementVerifiers.remove(element);
		}
		else if("visibility".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"visible","hidden","collapse","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("voice-family".equalsIgnoreCase(element))
		{      
			//generic-voice & //specific-voice
			auxilaryVerifiers[52]=new CSSPropertyVerifier(new String[]{"male","female","child"},new String[]{"st"},null,true);
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"inherit"},null,ElementInfo.AURALMEDIA,new String[]{"52<0,"+ElementInfo.UPPERLIMIT+"> 52"}));
			allelementVerifiers.remove(element);
			//53 54 55 56 59
		}
		else if("volume".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"silent","x-soft","soft","medium","loud","x-loud","inherit"},ElementInfo.AURALMEDIA,new String[]{"re","le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("white-space".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"normal","pre","nowrap","pre-wrap","pre-line","inherit"},ElementInfo.VISUALMEDIA));
			allelementVerifiers.remove(element);
		}
		else if("widows".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.BLOCKLEVELELEMENTS,new String[] {"inherit"},ElementInfo.VISUALMEDIA,new String[]{"in"}));
			allelementVerifiers.remove(element);
		}
		else if("width".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le","pe"}));
			allelementVerifiers.remove(element);
		}
		else if("word-spacing".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"normal","inherit"},ElementInfo.VISUALMEDIA,new String[]{"le"}));
			allelementVerifiers.remove(element);
		}
		else if("z-index".equalsIgnoreCase(element))
		{      
			elementVerifiers.put(element,new CSSPropertyVerifier(ElementInfo.HTMLELEMENTSARRAY,new String[] {"auto","inherit"},ElementInfo.VISUALMEDIA,new String[]{"in"}));
			allelementVerifiers.remove(element);
		}


	}


	/*
	 * This function returns the Verifier for a property. If it is not already loaded in the elementVerifier, then it is loaded and then returned to the caller.
	 */
	private static CSSPropertyVerifier getVerifier(String element)
	{
		element=element.toLowerCase();
		if(elementVerifiers.get(element)!=null)
			return elementVerifiers.get(element);
		else if(allelementVerifiers.contains(element))
		{
			addVerifier(element);
			return elementVerifiers.get(element);
		}
		else
			return null;
	}
	/*
	 * This function accepts media, list of HTML elements, CSS property and value and determines whether it is valid or not.
	 * @media print
	 * {
	 * h1, h2 , h4, h4 {font-size: 10pt}
	 * }
	 * media: print
	 * elements: [h1, h2, h3, h4]
	 * token: font-size
	 * value: 10pt
	 *
	 */
	private static boolean verifyToken(String media,String[] elements,String token,String value)
	{

		CSSPropertyVerifier obj=getVerifier(token);
		if(obj==null)
		{
			return false;
		}
		return obj.checkValidity(media, elements, value);

	}
	/*
	 * This function accepts an HTML element(along with class name, ID, pseudo class and attribute selector) and determines whether it is valid or not.
	 */
	public String HTMLelementVerifier(String elementString)
	{
		String HTMLelement="",pseudoClass="",className="",id="";
		boolean isValid=true;
		StringBuffer fBuffer=new StringBuffer();
		String attSelection="";
		if(elementString.indexOf('[')!=-1 && elementString.indexOf(']')!=-1 && (elementString.indexOf('[')<elementString.indexOf(']')))
		{
			attSelection=elementString.substring(elementString.indexOf('[')+1,elementString.indexOf(']')).trim();
			StringBuffer buf=new StringBuffer(elementString);
			buf.delete(elementString.indexOf('['), elementString.indexOf(']')+1);
			elementString=buf.toString();
			if(debug) log("attSelection="+attSelection+"  elementString="+elementString);
		}
		if(elementString.indexOf(':')!=-1)
		{
			int index=elementString.indexOf(':');
			if(index!=elementString.length()-1)
			{
				pseudoClass=elementString.substring(index+1,elementString.length()).trim();
				HTMLelement=elementString.substring(0,index).trim();
				if(debug) log("pseudoclass="+pseudoClass+" HTMLelement="+HTMLelement);
			}
			else
			{
				HTMLelement=elementString.trim();
			}
		}
		else
			HTMLelement=elementString.trim();

		if(HTMLelement.indexOf('.')!=-1)
		{
			int index=HTMLelement.indexOf('.');
			if(index!=HTMLelement.length()-1)
			{
				className=HTMLelement.substring(index+1,HTMLelement.length()).trim();
				HTMLelement=HTMLelement.substring(0,index).trim();
				if(debug) log("class="+className+" HTMLelement="+HTMLelement);
			}

		}
		else if(HTMLelement.indexOf('#')!=-1)
		{
			int index=HTMLelement.indexOf('#');
			if(index!=HTMLelement.length()-1)
			{
				id=HTMLelement.substring(index+1,HTMLelement.length()).trim();
				HTMLelement=HTMLelement.substring(0,index).trim();
				if(debug) log("id="+id+" element="+HTMLelement);
			}

		}

		if((ElementInfo.isValidHTMLTags(HTMLelement)) || ("".equals(HTMLelement.trim()) && (className!="" || id!="")))
		{
			if(className!="")
			{
				if(!ElementInfo.isValidName(className))
					isValid=false;
			}
			else if(id!="")
			{
				if(!ElementInfo.isValidName(id))
					isValid=false;
			}

			if(isValid && pseudoClass!="")
			{
				if(!ElementInfo.isValidPseudoClass(pseudoClass))
					isValid=false;
			}

			if(isValid && attSelection!="")
			{
				String[] attSelectionParts;
				
				if(attSelection.indexOf("|=")!=-1)
				{
					attSelectionParts=new String[2];
					attSelectionParts[0]=attSelection.substring(0,attSelection.indexOf("|="));
					attSelectionParts[1]=attSelection.substring(attSelection.indexOf("|=")+3,attSelection.length());
				}
				else if(attSelection.indexOf("~=")!=-1)
					attSelectionParts=attSelection.split("~=");
				else
					attSelectionParts=attSelection.split("=");
				
				
				//Verifying whether each character is alphanumeric or _
				if(debug) log("HTMLelementVerifier length of attSelectionParts="+attSelectionParts.length);
				
				if(attSelectionParts[0].length()==0)
					isValid=false;
				else
				{
					char c=attSelectionParts[0].charAt(0);
					if(!((c>='a' && c<='z') || (c>='A' && c<='Z')))
						isValid=false;
					for(int i=1;i<attSelectionParts[0].length();i++)
					{
						if(!((c>='a' && c<='z') || (c>='A' && c<='Z') || c=='_' || c=='-'))
							isValid=false;
					}
				}
			}


			if(isValid)
			{
				fBuffer.append(HTMLelement);
				if(className!="")
					fBuffer.append("."+className);
				else if(id!="")
					fBuffer.append("#"+id);
				if(pseudoClass!="")
					fBuffer.append(":"+pseudoClass);
				if(attSelection!="")
					fBuffer.append("["+attSelection+"]");
				return fBuffer.toString();
			}
		}

		return null;
	}
	/*
	 * This function works with different operators, +, >, " " and verifies each HTML element with HTMLelementVerifier(String elementString)
	 * e.g. div > p:first-child
	 * This would call HTMLelementVerifier with div and p:first-child
	 */
	public String recursiveSelectorVerifier(String selectorString)
	{
		selectorString=selectorString.toLowerCase().trim();
		if(selectorString.indexOf(" ")==-1 && selectorString.indexOf(">")==-1 && selectorString.indexOf("+")==-1)
			return HTMLelementVerifier(selectorString);
		int plusIndex,gtIndex,spaceIndex;
		plusIndex=selectorString.indexOf("+");
		if(plusIndex==-1)
			plusIndex=50000;
		gtIndex=selectorString.indexOf(">");
		if(gtIndex==-1)
			gtIndex=50000;
		spaceIndex=selectorString.indexOf(" ");
		if(spaceIndex==-1)
			spaceIndex=50000;
		String[] parts=new String[2];
		int index;
		char selector;
		if((plusIndex<gtIndex) && (plusIndex<spaceIndex))
		{
			index=plusIndex;
			selector='+';
		}
		else if((gtIndex<plusIndex) && (gtIndex<spaceIndex))
		{
			index=gtIndex;
			selector='>';
		}
		else
		{
			index=spaceIndex;
			selector=' ';
		}

		parts[0]=selectorString.substring(0,index);
		parts[1]=selectorString.substring(index+1,selectorString.length());
		if(debug) log("recursiveSelectorVerifier parts[0]=" + parts[0]+" parts[1]="+parts[1]);
		parts[0]=recursiveSelectorVerifier(parts[0]);
		parts[1]=recursiveSelectorVerifier(parts[1]);
		if(parts[0]!=null && parts[1]!=null)
			return parts[0]+selector+parts[1];
		else
			return null;

	}



	// main function
	public void filterCSS() throws IOException {

		final int STATE1=1; //State corresponding to @page,@media etc
		final int STATE2=2; //State corresponding to HTML element like body
		final int STATE3=3; //State corresponding to CSS properties

		/* e.g.
		 * STATE1
		 * @media screen {
		 * STATE2	STATE3
		 * h2 		{text-align:left;}
		 * }
		 */
		final int STATECOMMENT=4;
		final int STATE1INQUOTE=5;
		final int STATE2INQUOTE=6;
		final int STATE3INQUOTE=7;
		char currentQuote='"';
		int stateBeforeComment=0;
		int currentState=1;
		boolean isState1Present=false;
		String elements[]=null;
		StringBuffer filteredTokens=new StringBuffer();
		StringBuffer buffer=new StringBuffer();
		int openBraces=0;
		String defaultMedia="screen";
		String currentMedia=defaultMedia;
		String propertyName="",propertyValue="";
		boolean ignoreElementsS1=false,ignoreElementsS2=false;
		int x;
		char c=0,prevc=0;
		boolean s2Comma=false;
		boolean canImport=true; //import statement can occur only in the beginning

		while(true)
		{
			try
			{
				x=r.read();
			}
			catch(IOException e)
			{
				throw e;
			}

			if(x==-1)
			{
				if(filteredTokens.toString().trim().length()!=0)
					w.write("<!--(Deleted unfinished elements)"+filteredTokens.toString()+"-->");
				w.flush();
				break;
			}
			prevc=c;
			c=(char) x;
			if(debug) log("Read: "+c);
			if(prevc=='/' && c=='*' && currentState!=STATE1INQUOTE && currentState!=STATE2INQUOTE && currentState!=STATE3INQUOTE)
			{
				stateBeforeComment=currentState;
				currentState=STATECOMMENT;
				if(buffer.charAt(buffer.length()-1)=='/')
				{
					buffer.deleteCharAt(buffer.length()-1);
				}
				if(debug) log("Comment detected: buffer="+buffer);
			}
			switch(currentState)
			{
			case STATE1: 
				switch(c){
				case '\n':
				case ' ':
				case '\t':
					buffer.append(c);
					if(debug) log("STATE1 CASE whitespace: "+c);
					break;

				case '@':
					isState1Present=true;
					buffer.append(c);
					if(debug) log("STATE1 CASE @: "+c);
					break;

				case '{':
					openBraces++;
					isState1Present=false;
					String[] parts=buffer.toString().split(" ");
					if(parts.length!=2)
					{
						ignoreElementsS1=true;
						if(debug) log("STATE1 CASE {: Does not have two parts. ignoring "+buffer.toString());
						break;
					}
					else
					{
						if(verifyToken(null,null,parts[0].trim(),parts[1].trim())==true)
						{
							filteredTokens.append(buffer.toString()+"{ ");
							if(debug) log("filterCSS Media verified:"+parts[0].toLowerCase());
							if("@media".equals(parts[0].toLowerCase()))							{
								if(debug) log("filterCSS Media of the document changed to"+parts[1]);
								currentMedia=parts[1];
							}
						}
						else
						{
							ignoreElementsS1=true;
							if(debug) log("STATE1 CASE {: Failed verification test. ignoring "+buffer.toString());

						}
					}
					s2Comma=false;
					currentState=STATE2;
					buffer.setLength(0);
					break;
				case ';':
					//should be @import

					if(canImport && buffer.toString().contains("@import"))
					{
						if(debug) log("STATE1 CASE ;statement="+buffer.toString());
						
						String strbuffer=buffer.toString().toLowerCase().trim();
						int importIndex=strbuffer.indexOf("@import");
						if("".equals(strbuffer.substring(0,importIndex).trim()))
						{
							String str1=strbuffer.substring(importIndex+7,strbuffer.length());
							String[] strparts=str1.trim().split(" ");
							strparts=FilterUtils.removeWhiteSpace(strparts);
							String uri=null;
							if(strparts[0].contains("url("))
							{
								int firstIndex=strparts[0].indexOf("url(");
								int secondIndex=strparts[0].lastIndexOf(")");
								if("".equals(strparts[0].substring(0,firstIndex).trim()) && "".equals(strparts[0].substring(secondIndex+1,strparts[0].length())))
										uri=strparts[0].substring(firstIndex+4,secondIndex);
							}
							else
								uri=strparts[0];
							if(uri!=null && (strparts.length==1 || (strparts.length==2 && getVerifier("@media").checkValidity(null, null, strparts[1]))))
							{

								if(isValidURI(uri))
								w.write(buffer.toString()+"\n");
							}
						}
					}
					isState1Present=false;
					buffer.setLength(0);
					break;
				case '"':
				case '\'':
					buffer.append(c);
					currentState=STATE1INQUOTE;
					currentQuote=c;
					break;
				default:
					buffer.append(c);
				if(!isState1Present)
				{
					currentState=STATE2;	
				}
				if(debug) log("STATE1 default CASE: "+c);
				break;

				}
				break;

			case STATE1INQUOTE:
				if(debug) log("STATE1INQUOTE: "+c);
				switch(c)
				{
				case '"':
					if(currentQuote=='"' && prevc!='\\')
						currentState=STATE1;
				case '\'':
					if(currentQuote=='\'' && prevc!='\\')
						currentState=STATE1;
				default:
					buffer.append(c);
				break;
				}
				break;



			case STATE2:
				canImport=false;
				switch(c)
				{
				case '{':
					openBraces++;
					if(buffer.toString().trim()!="")
					{
						String filtered=recursiveSelectorVerifier(buffer.toString());
						if(filtered!=null)
						{
							if(s2Comma)
							{
								filteredTokens.append(",");
								s2Comma=false;
							}
							filteredTokens.append(" "+filtered);
							filteredTokens.append(" {");
						}
						else
						{
							ignoreElementsS2=true;
						}
						if(debug) log("STATE2 CASE { filtered elements"+filtered);
					}
					currentState=STATE3;
					buffer.setLength(0);
					break;

				case ',':
					String filtered=recursiveSelectorVerifier(buffer.toString());
					if(debug) log("STATE2 CASE , filtered elements"+filtered);
					if(filtered!=null)
					{
						if(s2Comma)
						{
							filteredTokens.append(", "+ filtered);
						}
						else
						{
							filteredTokens.append(filtered);
							s2Comma=true;
						}
					}
					buffer.setLength(0);
					break;	


				case '}':
					openBraces--;
					if(ignoreElementsS1)
						ignoreElementsS1=false;
					else
						w.write(filteredTokens.toString()+"}\n");
					filteredTokens.setLength(0);
					buffer.setLength(0);
					currentMedia=defaultMedia;
					isState1Present=false;
					currentState=STATE1;
					if(debug) log("STATE2 CASE }: "+c);
					break;

				case '"':
				case '\'':
					buffer.append(c);
					currentState=STATE2INQUOTE;
					currentQuote=c;
					break;

				default:
					buffer.append(c);
				if(debug) log("STATE2 default CASE: "+c);
				break;
				}
				break;

			case STATE2INQUOTE:
				if(debug) log("STATE2INQUOTE: "+c);
				switch(c)
				{
				case '"':
					if(currentQuote=='"'&& prevc!='\\')
						currentState=STATE2;
				case '\'':
					if(currentQuote=='\''&& prevc!='\\')
						currentState=STATE2;
				default:
					buffer.append(c);
				break;
				}
				break;

			case STATE3:
				switch(c)
				{
				case ':':
					propertyName=buffer.toString().trim();
					buffer.setLength(0);
					if(debug) log("STATE3 CASE :: "+c);
					break;

				case ';':
					propertyValue=buffer.toString().trim();
					buffer.setLength(0);
					if(!ignoreElementsS2 && verifyToken(currentMedia,elements,propertyName,propertyValue))
					{
						filteredTokens.append(" "+propertyName+":"+propertyValue+";");
						if(debug) log("STATE3 CASE ;: appending "+ propertyName+":"+propertyValue);
					}
					propertyName="";
					break;
				case '}':
					openBraces--;
					if(propertyName!="")
					{
						propertyValue=buffer.toString().trim();
						buffer.setLength(0);
						if(debug) log("Found PropertyName:"+propertyName+" propertyValue:"+propertyValue);
						if(!ignoreElementsS2 && verifyToken(currentMedia,elements,propertyName,propertyValue))
						{
							filteredTokens.append(" "+propertyName+":"+propertyValue+";");
							if(debug) log("STATE3 CASE }: appending "+ propertyName+":"+propertyValue);
						}
						propertyName="";

					}
					if(!ignoreElementsS2)
						filteredTokens.append("}\n");
					else
						ignoreElementsS2=false;
					if(openBraces==0)
					{
						w.write(filteredTokens.toString());
						filteredTokens.setLength(0);
					}
					currentState=STATE2;
					buffer.setLength(0);
					s2Comma=false;
					if(debug) log("STATE3 CASE }: "+c);
					break;

				case '"':
				case '\'':
					buffer.append(c);
					currentState=STATE3INQUOTE;
					currentQuote=c;
					break;

				default:
					buffer.append(c);
				if(debug) log("STATE3 default CASE : "+c);
				break;

				}
				break;

			case STATE3INQUOTE:
				if(debug) log("STATE3INQUOTE: "+c);
				switch(c)
				{
				case '"':
					if(currentQuote=='"'&& prevc!='\\')
						currentState=STATE3;
				case '\'':
					if(currentQuote=='\''&& prevc!='\\')
						currentState=STATE3;
				default:
					buffer.append(c);
				break;
				}
				break;

			case STATECOMMENT:
				switch(c)
				{
				case '/':
					if(prevc=='*')
					{
						currentState=stateBeforeComment;
						if(debug) log("Exiting the comment state");
					}
					break;
				}
				break;


			}

		}


	}

	public boolean anglecheck(String value)
	{
		boolean isValid=true;
		int index=-1;
		if(value.indexOf("deg")>-1)
		{
			index=value.indexOf("deg");
			String secondpart=value.substring(index,value.length()).trim();
			if(debug) log("found deg: second part:"+secondpart+":" );
			if(!("deg".equals(secondpart)))
				isValid=false;
		}
		else if(value.indexOf("grad")>-1)
		{
			index=value.indexOf("grad");
			String secondpart=value.substring(index,value.length()).trim();
			if(debug) log("found grad: second part:"+secondpart+":" );
			if(!("grad".equals(secondpart)))
				isValid=false;
		}
		else if(value.indexOf("rad")>-1)
		{
			index=value.indexOf("rad");
			String secondpart=value.substring(index,value.length()).trim();
			if(debug) log("found rad: second part:"+secondpart+":" );
			if(!("rad".equals(secondpart)))
				isValid=false;
		}
		if(index!=-1 && isValid)
		{
			String firstPart=value.substring(0,index);
			try
			{
				if(debug) log("Angle Value: first part:"+firstPart);
				float temp=Float.parseFloat(firstPart);
				return true;
			}
			catch(Exception e)
			{
				if(debug) log("Could not convert the String to angle value");

			}
		}
		return false;
	}

	/*
	 * Function to remove quotes.
	 */
	public static String removeQuotes(String value)
	{
		boolean firstQuote=false;
		StringBuffer newValue=new StringBuffer();
		int i;
		for(i=0;i<value.length();i++)
		{
			if(value.charAt(i)=='"')
			{ 
				if(!firstQuote)
					firstQuote=true;
				else
				{
					if(i<value.length())
					{
						if("".equals(value.substring(i+1,value.length()).trim()))
							break;
					}
					else
					{
						return newValue.toString();
					}

				}
			}
			else
				newValue.append(value.charAt(i));
		}
		for(int j=i+1;j<value.length();j++)
			newValue.append(value.charAt(j));
		return newValue.toString();



	}

	/*
	 * Basic class to verify value for a CSS Property. This class can verify values which are
	 * Integer,Real,Percentage, <Length>, <Angle>, <Color>, <URI>, <Shape> and so on.
	 * parserExpression is used for verifying regular expression for Property value
	 * e.g. [ <color> | transparent]{1,4}.
	 */
	static class CSSPropertyVerifier
	{
		public HashSet<String> allowedElements=null; // HashSet for all valid HTML elements which can have this CSS property
		public HashSet<String> allowedValues=null; //HashSet for all String constants that this CSS property can assume like "inherit"
		public HashSet<String> allowedMedia=null; // HashSet for all valid Media for this CSS property.
		/*
		 * in, re etc stands for different code strings using which these boolean values can be set in
		 * constructor like passing in,re would set isInteger and isReal.
		 */
		public boolean isInteger=false; //in
		public boolean isReal=false;	//re
		public boolean isPercentage=false;	//pe
		public boolean isLength=false;	//le
		public boolean isAngle=false;	//an	
		public boolean isColor=false; //co	
		public boolean isURI=false;	//ur
		public boolean isShape=false;	//sh	
		public boolean isString=false;//st
		public boolean isCounter=false; //co
		public boolean isIdentifier=false; //id
		public boolean isTime=false; //ti
		public boolean isFrequency=false; //fr
		public boolean onlyValueVerifier=false;
		public String[] cssPropertyList=null; 
		public String[] parserExpressions=null;
		public static FilterCallback cb;
		public static boolean debug;
		CSSPropertyVerifier()
		{}


		CSSPropertyVerifier(String[] allowedElements,String[] allowedValues,String[] allowedMedia)
		{
			this(allowedElements,allowedValues,allowedMedia,null,null);
		}


		CSSPropertyVerifier(String[] allowedValues,String[] possibleValues,String[] expression,boolean onlyValueVerifier)
		{
			this(null,allowedValues,null,possibleValues,expression);
			this.onlyValueVerifier=onlyValueVerifier;

		}

		CSSPropertyVerifier(String[] allowedElements,String[] allowedValues,String[] allowedMedia,String[] possibleValues,String[] parseExpression)
		{
			if(possibleValues!=null)
			{
				for(String possibleValue:possibleValues)
				{
					if("in".equals(possibleValue))
						isInteger=true; //in
					else if("re".equals(possibleValue))
						isReal=true;	//re
					else if("pe".equals(possibleValue))
						isPercentage=true;	//pe
					else if("le".equals(possibleValue))
						isLength=true;	//le
					else if("an".equals(possibleValue))
						isAngle=true;	//an
					else if("co".equals(possibleValue))
						isColor=true; //co
					else if("ur".equals(possibleValue))
						isURI=true;	//ur
					else if("sh".equals(possibleValue))
						isShape=true;	//sh
					else if("st".equals(possibleValue))
						isString=true;//st
					else if("co".equals(possibleValue))
						isCounter=true; //co
					else if("id".equals(possibleValue))
						isIdentifier=true; //id
					else if("ti".equals(possibleValue))
						isTime=true; //ti
					else if("fr".equals(possibleValue))
						isFrequency=true;
				}
			}
			if(allowedElements!=null)
			{
				this.allowedElements= new HashSet<String>();
				for(int i=0;i<allowedElements.length;i++)
					this.allowedElements.add(allowedElements[i]);
			}

			if(allowedValues!=null)
			{
				this.allowedValues=new HashSet<String>();
				for(int i=0;i<allowedValues.length;i++)
					this.allowedValues.add(allowedValues[i]);
			}

			if(allowedMedia!=null)
			{
				this.allowedMedia=new HashSet<String>();
				for(int i=0;i<allowedMedia.length;i++)
					this.allowedMedia.add(allowedMedia[i]);
			}
			if(parseExpression!=null)
				this.parserExpressions=parseExpression.clone();
			else
				this.parserExpressions=null;

		}


		CSSPropertyVerifier(String[] allowedElements,String[] allowedValues,String[] allowedMedia,String[] possibleValues)
		{

			this(allowedElements,allowedValues,allowedMedia,possibleValues,null);
		}





		public static boolean isIntegerChecker(String value)
		{
			try{
				Integer.parseInt(value); //CSS Property has a valid integer.
				return true;
			}
			catch(Exception e) {return false; }

		}


		public static boolean isRealChecker(String value)
		{
			try
			{
				Float.parseFloat(value); //Valid float
				return true;
			}
			catch(Exception e){return false; }
		}
		public void log(String s)
		{
			Logger.debug(this,"CSSPropertyVerifier "+s);
			//System.out.println("CSSPropertyVerifier "+s);
		}

		public static boolean isValidURI(String URI)
		{
			//if(debug) log("CSSPropertyVerifier isVaildURI called cb="+cb);
			try
			{
				//if(debug) log("CSSPropertyVerifier isVaildURI "+cb.processURI(URI, null));
				return URI.equals(cb.processURI(URI, null));
			}
			catch(CommentException e)
			{
				//if(debug) log("CSSPropertyVerifier isVaildURI Exception"+e.toString());
				return false;
			}

		}

		public boolean checkValidity(String value)
		{
			return this.checkValidity(null,null, value);
		}
		// Verifies whether this CSS property can have a value under given media and HTML elements
		public boolean checkValidity(String media,String[] elements,String value )
		{

			if(!onlyValueVerifier)
			{
				if(allowedMedia!=null && !allowedMedia.contains(media.trim().toLowerCase()))
				{
					if(debug) log("checkValidity Media of the element is not allowed.Media="+media+" allowed Media="+allowedMedia.toString());

					return false;
				}
				if(elements!=null)
				{
					if(allowedElements!=null)
					{
						for(String element:elements)
						{

							if(!allowedElements.contains(element.trim().toLowerCase()))
							{
								if(debug) log("checkValidity: element is not allowed:"+element);
								return false;
							}
						}
					}
				}
			}


			String originalValue=value;
			value=HTMLFilter.stripQuotes(value).toLowerCase().trim();

			if(allowedValues!=null && allowedValues.contains(value)) //CSS Property has one of the explicitly defined values
				return true;

			if(isInteger && isIntegerChecker(value))
			{
				return true;
			}

			if(isReal && isRealChecker(value))
			{
				return true;
			}

			if(isPercentage && FilterUtils.isPercentage(value)) //Valid percentage X%
			{
				return true;
			}

			if(isLength && FilterUtils.isLength(value,false)) //Valid unit Vxx where xx is unit or V
			{
				return true;
			}

			if(isAngle && FilterUtils.isAngle(value))
			{
				return true;
			}
			else if(isColor)
			{
				if(FilterUtils.isColor(value, false))
					return true;
			}

			if(isURI) 
			{
				if(value.indexOf("url(")>-1)
				{

					int firstIndex=value.indexOf("url(");
					if("".equals(value.substring(0,firstIndex).trim()))
					{
						int secondIndex=value.lastIndexOf(")");
						if(secondIndex>firstIndex && "".equals(value.substring(secondIndex+1, value.length()).trim()))
						{
							String url=value.substring(firstIndex+1,secondIndex);
							return isValidURI(CSSTokenizerFilter.removeQuotes(url));
						}

					}
					return false;
				}
			}

			if(isShape)
			{
				if(FilterUtils.isValidCSSShape(value))
					return true;
			}

			if(isFrequency)
			{
				if(FilterUtils.isFrequency(value))
					return true;
			}

			if(isIdentifier)
			{
				if(ElementInfo.isValidIdentifier(value))
					return true;
			}

			if(isString)
			{
				if(ElementInfo.isValidString(value))
					return true;
			}

			/*
			 * Parser expressions
			 * 1 || 2 => 1a2
			 * [1][2] =>1 2
			 * 1<1,4> => 1<1,4>
			 * 1* => 1<0,65536>
			 * 1? => 1o
			 * 
			 */  
			/*
			 * For each parserExpression, recursiveParserExpressionVerifier() would be called with parserExpression and value.
			 */
			if(parserExpressions!=null)
			{

				for(String parserExpression:parserExpressions)
				{
					boolean result=recursiveParserExpressionVerifier(parserExpression,originalValue);

					if(result)
						return true;
				}
			}

			return false;
		}
		/* parserExpression string would be interpreted as 1 || 2 => 1a2 here 1a2 would be written to parse 1 || 2 where 1 and 2 are auxilaryVerifiers[1] and auxilaryVerifiers[2] respectively i.e. indices in auxilaryVerifiers
		 * [1][2] =>1b2
		 * 1<1,4> => 1<1,4>
		 * 1* => 1<0,65536>
		 * 1? => 1o
		 * 1+=>1<1,65536>
		 * Additional expressions that can be passed to the function
		 * 1 2 => both 1 and 2 should return true where 1 and 2 are again indices in auxiliaryVerifier array.
		 * [a,b]=> give at least a tokens and at the most b tokens(part of values) to this block of expression.
		 * e.g. 1[2,3] and the value is "hello world program" then object 1 would be tested with "hello world"
		 * and "hello world program".
		 * The main logic of this function is find a set of values for different part of the ParserExpression so that each part returns true.
		 * e.g. Suppose the expression is
		 * (1 || 2) 3
		 * where object 1 can consume upto 2 tokens, 2 can consume upto 2 tokens and 3 would consume one and only one token.
		 * This expression would be encoded as
		 * "1a2" for 1 || 2 Using this, third object would be created say 4 e.g. 4="1a2"
		 * Now the main object would be given the parserExpression as
		 * "4<0,4> 3"
		 * This function would call
		 * 4 with 0 tokens and 3 with the remaining
		 * 4 with 1 tokens and 3 with the remaining
		 * and so on.
		 * If all combinations are failed then it would return false. If any combination gives true value
		 * then return value would be true.
		 */
		public boolean recursiveParserExpressionVerifier(String expression,String value)
		{
			if(debug) log("1recursiveParserExpressionVerifier called: with "+expression+" "+value);
			if((expression==null || ("".equals(expression.trim()))))
			{
				if((value==null || ("".equals(value.trim()))))
					return true;
				else 
					return false;
			}
			ArrayList<String> doubleQuotedValues=new ArrayList<String>();
			while(value.indexOf('"')!=-1)
			{
				int first,second;
				first=value.indexOf('"');
				second=value.indexOf('"',first+1);
				if(second==-1)
					return false;
				else
				{
					String temp=value.substring(first,second+1);
					temp=CSSTokenizerFilter.removeQuotes(temp);
					temp=temp.trim().toLowerCase();
					doubleQuotedValues.add(temp);
				}

				StringBuffer newValue=new StringBuffer();
				newValue.append(value.substring(0, first));
				if(second!=value.length()-1)
					newValue.append(value.substring(second+1,value.length()));
				value=newValue.toString();
			}
			
			String[] valueParts=value.trim().split(" ");
			valueParts=FilterUtils.removeWhiteSpace(valueParts);
			valueParts=CSSTokenizerFilter.concat(valueParts,doubleQuotedValues.toArray(new String[0]));
			expression=expression.trim();
			
			int tokensCanBeGivenLowerLimit=1,tokensCanBeGivenUpperLimit=1;
			for(int i=0;i<expression.length();i++)
			{
				if(expression.charAt(i)=='a') //Identifying ||
				{
					int noOfa=0;
					int endIndex=expression.length();
					//Detecting the other end
					for(int j=0;j<expression.length();j++)
					{
						if(expression.charAt(j)=='?' || expression.charAt(j)=='<' || expression.charAt(j)=='>' || expression.charAt(j)==' ')
						{
							endIndex=j;
							break;
						}
						else if(expression.charAt(j)=='a')
							noOfa++;

					}
					String firstPart=expression.substring(0,endIndex);
					String secondPart="";
					if(endIndex!=expression.length())
						secondPart=expression.substring(endIndex+1,expression.length());
					for(int j=1;j<=noOfa+1 && j<=valueParts.length;j++)
					{
						if(debug) log("2Making recursiveDoubleBarVerifier to consume "+j+" words");
						StringBuffer partToPassToDB=new StringBuffer("");
						for(int k=0;k<j;k++)
						{
							partToPassToDB.append(valueParts[k]+" ");
						}
						if(debug) log("3Calling recursiveDoubleBarVerifier with "+firstPart+" "+partToPassToDB.toString());
						if(recursiveDoubleBarVerifier(firstPart,partToPassToDB.toString())) //This function is written to verify || operator.
						{
							StringBuffer partToPass=new StringBuffer("");
							for(int k=j;k<valueParts.length;k++)
								partToPass.append(valueParts[k]+" ");
							if(debug) log("4recursiveDoubleBarVerifier true calling itself with "+secondPart+partToPass.toString());
							if(recursiveParserExpressionVerifier(secondPart,partToPass.toString()))
								return true;
						}
						if(debug) log("5Back to recursiveDoubleBarVerifier "+j+" "+(noOfa+1)+" "+valueParts.length);
					}
					return false;
				}
				else if(expression.charAt(i)==' ')
				{
					String firstPart=expression.substring(0,i);
					String secondPart=expression.substring(i+1,expression.length());
					if(valueParts!=null && valueParts.length>0)
					{
						int index=Integer.parseInt(firstPart);
						boolean result=CSSTokenizerFilter.auxilaryVerifiers[index].checkValidity(valueParts[0]);
						if(result)
						{
							StringBuffer partToPass=new StringBuffer();
							for(int a=1;a<valueParts.length;a++)
								partToPass.append(valueParts[a]+" ");
							if(debug) log("8First part is true. partToPass="+partToPass.toString());
							if(recursiveParserExpressionVerifier(secondPart,partToPass.toString()))
								return true;
						}
					}
					return false;
				}
				else if(expression.charAt(i)=='?')
				{
					String firstPart=expression.substring(0,i);
					String secondPart=expression.substring(i+1,expression.length());
					int index=Integer.parseInt(firstPart);
					if(valueParts.length>0)
					{
						boolean result= CSSTokenizerFilter.auxilaryVerifiers[index].checkValidity(valueParts[0]);
						if(result)
						{
							StringBuffer partToPass=new StringBuffer();
							for(int a=1;a<valueParts.length;a++)
								partToPass.append(valueParts[a]+" ");
							if(recursiveParserExpressionVerifier(secondPart,partToPass.toString()))
								return true;
						}
					}
					else if(recursiveParserExpressionVerifier(secondPart,value))
						return true;

					return false;
				}
				else if(expression.charAt(i)=='<')
				{
					int tindex=expression.indexOf('>');
					if(tindex>i)
					{
						int firstIndex=tindex+1;
						if((tindex!=expression.length()-1) && expression.charAt(tindex+1)=='[')
						{
							int indexOfSecondBracket=expression.indexOf(']');
							if(indexOfSecondBracket>(tindex+1))
							{
								tokensCanBeGivenLowerLimit=Integer.parseInt(expression.substring(tindex+2,indexOfSecondBracket).split(",")[0]);
								tokensCanBeGivenUpperLimit=Integer.parseInt(expression.substring(tindex+2,indexOfSecondBracket).split(",")[1]);
								firstIndex=expression.indexOf(']')+1;
							}
						}
						String firstPart=expression.substring(0,i);
						String secondPart=expression.substring(firstIndex,expression.length());
						if(debug) log("9in < firstPart="+firstPart+" secondPart="+secondPart+" tokensCanBeGivenLowerLimit="+tokensCanBeGivenLowerLimit+" tokensCanBeGivenUpperLimit="+tokensCanBeGivenUpperLimit);
						int index=Integer.parseInt(firstPart);
						String[] strLimits=expression.substring(i+1,tindex).split(",");
						if(strLimits.length==2)
						{
							int lowerLimit=Integer.parseInt(strLimits[0]);
							int upperLimit=Integer.parseInt(strLimits[1]);
							for(int j=lowerLimit;j<=upperLimit;j++)
							{

								for(int l=tokensCanBeGivenLowerLimit;l<=tokensCanBeGivenUpperLimit;l++)
								{
									if(debug) log("recursiveParserExpressionVerifier j="+j+" l="+l);
									boolean result=recursiveVariableOccuranceVerifier(index,getSubArray(valueParts,0,j*l),lowerLimit,upperLimit,tokensCanBeGivenLowerLimit,tokensCanBeGivenUpperLimit);
									if(result)
									{
										if(debug) log("TRUE j="+j+" l="+l+" result is true");
										if(recursiveParserExpressionVerifier(secondPart,getStringFromArray(valueParts,j*l,valueParts.length)))
											return true;
									}
								}

							}
						}

					}

					return false;
				}

			}
			//Single verifier object
			if(debug) log("10Single token:"+expression);
			int index=Integer.parseInt(expression);
			return CSSTokenizerFilter.auxilaryVerifiers[index].checkValidity(value);


		}
		/*
		 * This function takes an array of string and concatenates everything in a " " seperated string.
		 */
		public static String getStringFromArray(String[] parts,int lowerIndex,int upperIndex)
		{
			StringBuffer buffer=new StringBuffer();
			if(parts!=null && lowerIndex<parts.length)
			{
				for(int i=lowerIndex;i<upperIndex && i<parts.length;i++)
					buffer.append(parts[i]+ " ");
				return buffer.toString();
			}
			else
				return "";

		}
		//Creates a new sub array from the main array and returns it.
		public static String[] getSubArray(String[] array,int lowerIndex,int upperIndex)
		{
			String[] arrayToReturn=new String[upperIndex-lowerIndex];
			if(array!=null && lowerIndex<array.length)
			{
				for(int i=lowerIndex;i<upperIndex && i<array.length;i++)
				{	
					arrayToReturn[i-lowerIndex]=array[i];
				}
				return arrayToReturn;
			}
			else
				return null;
		}
		/*
		 * For verifying part of the ParseExpression with [] operator.
		 */
		public boolean recursiveVariableOccuranceVerifier(int verifierIndex,String[] valueParts,int lowerLimit,int upperLimit,int tokensCanBeGivenLowerLimit,int tokensCanBeGivenUpperLimit)
		{

			if(valueParts==null || valueParts.length==0)
				return true;
			if(debug) log("recursiveVariableOccuranceVerifier called with verifierIndex="+verifierIndex+" valueParts="+getStringFromArray(valueParts,0,valueParts.length));
			for(int j=lowerLimit;j<=upperLimit;j++)
			{
				int k;
				for(k=0;k<j;k++)
				{

					for(int l=tokensCanBeGivenLowerLimit;l<=tokensCanBeGivenUpperLimit && k*l<valueParts.length;l++)
					{

						String valueArgument=getStringFromArray(valueParts,k*l,l);
						if(CSSTokenizerFilter.auxilaryVerifiers[verifierIndex].checkValidity(valueArgument))
						{
							if(debug) log("recursiveVariableOccuranceVerifier: "+l+" tokens can be consumed");
							boolean result=recursiveVariableOccuranceVerifier(verifierIndex,getSubArray(valueParts,k*l+l,valueParts.length),j,upperLimit,tokensCanBeGivenLowerLimit,tokensCanBeGivenUpperLimit);
							if(result)
								return true;
						}
					}
				}
			}
			return false;

		}

		/*
		 * This function verifies || operator. This function returns true only if it can consume entire value for the given parseExpression
		 * e.g. expression is [1 || 2 || 3 || 4] and value is "Hello world program"
		 * Then this function would try all combinations of objects so that the expression can consume this value.
		 * 1 would try to consume "Hello" and rest would try to consume "world program"
		 * 2 would try to consume "Hello" and rest would try to consume "world program"
		 * 3 would try to consume "Hello" and rest would try to consume "world program"
		 * and so on.
		 */
		public boolean recursiveDoubleBarVerifier(String expression,String value)
		{
			if(debug) log("11in recursiveDoubleBarVerifier expression="+expression+" value="+value);
			if((value==null || ("".equals(value.trim()))))
				return true;

			String[] valueParts=value.trim().split(" ");

			for(int i=0;i<expression.length();i++)
			{
				if(expression.charAt(i)=='a')
				{
					String firstPart=expression.substring(0,i);
					String secondPart=expression.substring(i+1,expression.length());
					if(debug) log("12in a firstPart="+firstPart+" secondPart="+secondPart);

					boolean result=recursiveDoubleBarVerifier(secondPart,value);
					if(result)
					{
						if(debug) log("13if part of the loop is true");
						return true;
					}

					if(1==2) {}
					else
					{
						int index=Integer.parseInt(firstPart);
						for(int j=0;j<valueParts.length;j++)
						{

							result=CSSTokenizerFilter.auxilaryVerifiers[index].checkValidity(valueParts[j]);
							if(debug) log("14in for loop result:"+result+" for "+valueParts[j]);
							if(result)
							{

								StringBuffer valueToPass=new StringBuffer();
								for(int k=0;k<valueParts.length;k++)
									if(k!=j)
										valueToPass.append(valueParts[k]+" ");
								if(debug) log("14a "+valueParts[j]+" can be consumed by "+index+ " passing on expression="+secondPart+ " value="+valueToPass.toString());
								result=recursiveDoubleBarVerifier(secondPart,valueToPass.toString());
								if(result)
								{
									if(debug) log("15else part is true, value consumed="+valueParts[j]);
									return true;
								}
							}

						}
					}
					return false;
				}
			}

			//Single token
			int index=Integer.parseInt(expression);
			if(debug) log("16Single token:"+expression+" with value=*"+value.trim()+"* validity="+CSSTokenizerFilter.auxilaryVerifiers[index].checkValidity(value));
			return CSSTokenizerFilter.auxilaryVerifiers[index].checkValidity(value.trim());


		}





	}
	//CSSPropertyVerifier class extended for verifying content property.
	static class contentPropertyVerifier extends CSSPropertyVerifier
	{

		contentPropertyVerifier(String[] allowedValues)
		{
			super(null,allowedValues,null,null,null);
		}

		@Override
		public boolean checkValidity(String media,String[] elements,String value )
		{
			if(debug) log("contentPropertyVerifier checkValidity called");

			value=HTMLFilter.stripQuotes(value).toLowerCase().trim();

			if(allowedValues!=null && allowedValues.contains(value))
				return true;

			//String processing
			if(ElementInfo.isValidString(value))
				return true;

			//Counter processing
			int counterEnds=value.indexOf("counter");
			if(counterEnds!=-1)
			{
				int firstIndex=value.indexOf("(");
				int secondIndex=value.indexOf(")");
				if(debug) log("contentPropertyVerifier firstIndex="+firstIndex+" secondIndex="+secondIndex+" value="+value);
				if(("".equals(value.substring(counterEnds+7, firstIndex).trim())) && ("".equals(value.substring(secondIndex+1, value.length()).trim())))
				{
					if(firstIndex<secondIndex)
					{
						String[] valueParts=value.substring(firstIndex+1,secondIndex).split(",");
						valueParts=FilterUtils.removeWhiteSpace(valueParts);
						if(debug) log("contentPropertyVerifier valuePartsLength="+valueParts.length);
						if(valueParts.length==1 || valueParts.length==2 || valueParts.length==3)
						{
							if(ElementInfo.isValidIdentifier(valueParts[0]))
							{
								if(valueParts.length==1)
									return true;
								HashSet<String> listStyleType=new HashSet<String>();
								listStyleType.add("disc");
								listStyleType.add("circle");
								listStyleType.add("square");
								listStyleType.add("decimal");
								listStyleType.add("decimal-leading-zero");
								listStyleType.add("lower-roman");
								listStyleType.add("upper-roman");
								listStyleType.add("disc lower-greek");
								listStyleType.add("lower-latin");
								listStyleType.add("upper-latin");
								listStyleType.add("armenian");
								listStyleType.add("georgian");
								listStyleType.add("lower-alpha");
								listStyleType.add("upper-alpha");
								listStyleType.add("none");
								if(listStyleType.contains(valueParts[1]) || ElementInfo.isValidString(valueParts[1]))
								{
									if(valueParts.length==2)
										return true;

									if(listStyleType.contains(valueParts[2]))
										return true;
								}
							}

						}

					}
				}
			}

			int attrIndex=value.indexOf("attr");
			if(attrIndex!=-1)
			{
				int firstIndex=value.indexOf("(");
				int secondIndex=value.lastIndexOf(")");
				if(debug) log("contentPropertyVerifier attr found");
				if(("".equals(value.substring(0,firstIndex).trim())) && ("".equals(value.substring(secondIndex+1,value.length()).trim())))
				{
					String strIdentifier=value.substring(firstIndex+1, secondIndex);
					if(ElementInfo.isValidIdentifier(strIdentifier))
						return true;
				}

			}
			return false;

		}



	}

	//For verifying ’font-size’[ / ’line-height’]? of Font property

	static class FontPartPropertyVerifier extends CSSPropertyVerifier
	{
		@Override
		public boolean checkValidity(String value)
		{

			if(debug) log("FontPartPropertyVerifier called with "+value);
			CSSPropertyVerifier fontSize=new CSSPropertyVerifier(new String[] {"xx-small","x-small","small","medium","large","x-large","xx-large","larger","smaller","inherit"},new String[]{"le","pe"},null,true);
			if(value.indexOf("/")!=-1)
			{
				int slashIndex=value.indexOf("/");
				String firstPart=value.substring(0,slashIndex);
				String secondPart=value.substring(slashIndex+1,value.length());
				if(debug) log("FontPartPropertyVerifier FirstPart="+firstPart+" secondPart="+secondPart);
				CSSPropertyVerifier lineHeight=new CSSPropertyVerifier(new String[] {"normal","inherit"},new String[]{"le","pe","re","in"},null,true);
				if(fontSize.checkValidity(firstPart) && lineHeight.checkValidity(secondPart))
					return true;
			}
			else
				return fontSize.checkValidity(value);
			return false;

		}

	}


}
