package org.brailleblaster;
import org.brailleblaster.louisutdml.LouisFree;
import org.brailleblaster.wordprocessor.WPManager;
public class StartEnd
{

WPManager wpManager = new WPManager ();

public static void main (String[] args)
{
if (args.length == 0)
wpManager.normal ();
else
processArgs (args);
LouisFree.louisFree ();
}

private void processArgs (String[] args)
{
wpManager.special ();
}

}