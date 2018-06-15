/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.util;

import java.util.Hashtable;
import java.util.Vector;

/**
 * A class that does approximately what getopt() does in C.
 * <P>
 * This code is originally Copyright(C) Paul Jimenez, 1997,
 * but completely freely redistributable
 *
 * @version $Id: GetOpt.java,v 1.10 2007/09/18 18:58:11 ejfried Exp $
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class GetOpt
{

    /* parsed options are stored in here */
    private Hashtable optionlist;

    /* Constructor args are remembered in here */
    private String   flags;
    private String   options;
    private String[] argv;
    private String[] longflags;
    private String[] longoptions;
    private boolean unknownAreErrors = true;

    /* The list of parameters that weren't arguments to options */
    private Vector params = new Vector();

    /**
     * Parses a command line including both long and short options. All
     * arguments to options (long or short) are required. An isolated '--'
     * on the command line indicates the end of arguments.
     *
     * @param _argv array of strings (usually argv)
     * @param _flags a string of valid options that don't take arguments
     * @param _options a string of valid options that take arguments
     * @param _longflags array of strings that are valid options that don't
     *   take arguments
     * @param _longoptions array of strings that are valid options that
     *   take arguments
     * @exception GetOptException for any error.
     */

    public GetOpt(String _argv[],
                  String _flags, String _options,
                  String _longflags[], String _longoptions[])
        throws GetOptException
    {
        argv = _argv;
        flags = _flags;
        options = _options;
        longflags = _longflags;
        longoptions = _longoptions;

        doParse();
    }

    /**
     * Parses a command line including short options only. All
     * arguments to options are required. An isolated '--'
     * on the command line indicates the end of arguments.
     * @param _argv array of strings (usually argv)
     * @param _flags a string of valid options that don't take arguments
     * @param _options a string of valid options that take arguments
     * @exception GetOptException for any error.
     */

    public GetOpt(String _argv[],
                  String _flags,
                  String _options)
        throws GetOptException
    {
        String emptyarray[] = {};
        argv = _argv;
        flags = _flags;
        options = _options;
        longflags = emptyarray;
        longoptions = emptyarray;

        doParse();
    }

    /**
     * Parses a command line including long options only. All
     * arguments to options are required. Unknown options are returned as params -
     * they are NOT errors when this constructor is used!
     * @param _argv array of strings (usually argv)
     * @param _longflags array of strings that are valid options that don't
     *   take arguments
     * @param _longoptions array of strings that are valid options that
     *   take arguments
     * @exception GetOptException for any error.
     */

    public GetOpt(String _argv[],
                  String _longflags[], String _longoptions[])
        throws GetOptException
    {
        argv = _argv;
        flags = "";
        options = "";
        longflags = _longflags;
        longoptions = _longoptions;
        unknownAreErrors = false;

        doParse();
    }

    /**
     * Returns the list of command-line arguments that weren't flags,
     * options, or arguments, in left-to-right order
     */

    public String[] params()
    {
        String [] parr = new String[params.size()];
        params.copyInto(parr);
        return parr;
    }

    /**
     *Indicates whether the command line contained a specified option
     *@param option the option you want to check on
     *@return true only if option was specified on the command line
     */

    public boolean hasOption(char option)
    {
        return hasOption("" + option);
    }

    /**
     *Indicates whether the command line contained a specified option
     *@param option the option you want to check on
     *@return true only if option was specified on the command line
     */
    public boolean hasOption(String option)
    {
        return optionlist.containsKey(option);
    }

    /**
     * Returns the 'value' of an option
     * @param option the option whose argument you want
     * @return the argument specified for this option on the command line
     or null if the argument names a flag
    */

    public String getOption(char option)
    {
        return getOption("" + option);
    }

    /**
     * Returns the 'value' of an option
     * @param option the option whose argument you want
     * @return the argument specified for this option on the command line
     or null if the argument names a flag
    */

    public String getOption(String option)
    {
        String[] strings = (String[]) optionlist.get(option);
        return strings != null ? strings[0] : null;
    }


    /* ******************************************************************
     * Private methods from here down
     ******************************************************************** */

    private void doParse() throws GetOptException {

        int i;
        Hashtable possopts = new Hashtable();
        String arg, optarg;
        boolean moreargs = true;

        optionlist = new Hashtable();

        /* keep this O(length(possible options) + length(arguments) by
           pulling possopts into a hashtable */

        for(i = 0; i <longflags.length ; i++) {
            possopts.put(longflags[i], Boolean.FALSE);
        }
        for(i = 0; i <longoptions.length ; i++) {
            possopts.put(longoptions[i], Boolean.TRUE);
        }
        for(i = 0; i <flags.length() ; i++) {
            possopts.put("" + flags.charAt(i), Boolean.FALSE);
        }

        for(i = 0; i <options.length() ; i++) {
            possopts.put("" + options.charAt(i), Boolean.TRUE);
        }

        /* now that we're initialized, run thru the args */

        i = 0;
        while (i<argv.length) {
            arg = argv[i];
            if (moreargs) {
                /* haven't hit 'end of args' separator yet */
                if (arg.equalsIgnoreCase("--")) {
                                /* special case */
                    moreargs = false;
                }
                else if (isLongOpt(arg)) {
                                /* it's a long option */
                    arg = stripDashes(arg);

                    if (possopts.get(arg) == Boolean.FALSE) {
                        /* it's a flag */
                        setHasFlag(arg);
                    }
                    else {
                        /* it's an option */
                        setHasOption(arg, optarg = paramFor(argv, i++));
                        if (optarg == null)
                            throw new GetOptException("Missing argument " +
                                                      "to option " + arg);
                    }
                }
                else if (isOptList(arg)) {
                                /* it's an option list (shortopts) */
                    for (int j = 1; j <arg.length(); j++) {
                        /* cycle thru the shortopts */
                        /* get each one individually */
                        String charatj = "" + arg.charAt(j);
                        if (possopts.containsKey(charatj)) {
                            /* valid arg */
                            if (possopts.get(charatj) == Boolean.FALSE) {
                                /* it's a flag */
                                setHasFlag(charatj);
                            }
                            else {
                                /* it's an option */
                                setHasOption(charatj,
                                             optarg = paramFor(argv, i++));
                                if (optarg == null)
                                    throw new GetOptException("Missing " +
                                                              "argument " +
                                                              "for " +
                                                              charatj);
                            }
                        }
                        else
                            throw new GetOptException("Bad option: " +
                                                      charatj);
                    }
                }
                else {
                                /* it's a param */
                    params.addElement(arg);
                }
            }
            else {
                /* the rest of the args are params */
                params.addElement(arg);
            }
            i++;
        }
    }

    private String paramFor(String arglist[], int optat) {
        int idx = optat + 1;
        if (arglist.length <= idx)
            return null;
        else
            return arglist[idx];
    }

    private void setHasFlag(String s)
    {
        optionlist.put(s, new String[1]);
    }

    private void setHasOption(String o, String p)
    {
        String [] sa = null;
        String[] existing = (String[]) optionlist.get(o);
        if (existing == null) {
            sa = new String[]{p};
        } else {
            sa = new String[existing.length + 1];
            System.arraycopy(existing, 0, sa, 0, existing.length);
            sa[existing.length] = p;
        }
        
        optionlist.put(o, sa);
    }

    /*
      An option is an optlist if it's not a longopt, but starts with a dash
      and contains more than one character.
    */

    private boolean isOptList(String s)
    {
        if (isLongOpt(s) || !unknownAreErrors)
            return false;
        else
            return (s.length() > 1) && (s.charAt(0) == '-');
    }

    /*
      An option is a longopt if the characters starting with the first
      non-dash character occur as a word in the longopts array.
    */

    private boolean isLongOpt(String s) {
        if (!s.startsWith("-"))
            return false;
        String arg = stripDashes(s);
        return occursIn(arg, longoptions) || occursIn(arg, longflags);
    }

    private boolean occursIn(String s, String[] arr) {
        for (int i=0; i< arr.length; i++)
            if (s.equals(arr[i]))
                return true;
        return false;
    }

    private String stripDashes(String s)
    {
        int i = 0;
        while (s.charAt(i) == '-')
            i++;
        if (i >= s.length())
            return "";
        else
            return s.substring(i);
    }

    public String[] getOptions(String s) {
        return (String[]) optionlist.get(s);
    }
    
    public String getOption(String name, String dflt) {
    	if (hasOption(name))
    		return getOption(name);
    	else
    		return dflt;
    }
}
