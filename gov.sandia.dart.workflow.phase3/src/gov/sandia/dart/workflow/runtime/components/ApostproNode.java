/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class ApostproNode extends SAWCustomNode
{

  private static final String SKIP = "skip";
  private static final String FIELD = "field";
  private static final String MATCH = "match";
  private static final String SEPARATOR = "separator";
  private static final String INPUT_FILE = "inputFile";




  @Override
  protected Map<String, Object> doExecute(Map<String, String> properties,
                                          WorkflowDefinition workflow,
                                          RuntimeData runtime)
  {

    // allow optional specification of a separator
    String splitSep = "[,\\s]+";

    String optSep =
        getOptionalStringFromPortOrProperty(runtime, properties, SEPARATOR);

    if (StringUtils.isNotBlank(optSep)) {
      splitSep = optSep;
    }


    File inputFile = getInputFile(properties, runtime);
    String result = "NO_MATCH";
    try {
      Pattern p = Pattern.compile(getMatch(properties));
      List<String> lines = FileUtils.readLines(inputFile, StandardCharsets.UTF_8);
      int i = 0;
      for (i = 0; i < lines.size(); ++i) {
        String line = lines.get(i);

        if (p.matcher(line).find()) {
          int lineIndex = i + getSkip(properties);
          if (lineIndex >= lines.size()) {
            throw new SAWWorkflowException("Matched line index plus skip ("
                + lineIndex + ") extends past end of file");
          }
          String otherLine = lines.get(lineIndex).trim();
          String[] fields = otherLine.split(splitSep);
          int fieldIndex = getField(properties);
          if (fieldIndex >= fields.length) {
            throw new SAWWorkflowException("Field index (" + fieldIndex
                + ") past end of line \"" + otherLine + "\"");
          }
          result = fields[getField(properties)];
          break;
        }
      }
      return Collections.singletonMap("f", result);

    }
    catch (PatternSyntaxException e) {
      throw new SAWWorkflowException(
          "Invalid regular expression for parameter 'match'",
          e);
    }
    catch (IOException e) {
      throw new SAWWorkflowException(
          "Error reading file " + inputFile.getAbsolutePath(),
          e);
    }
  }




  @Override
  public List<InputPortInfo> getDefaultInputs()
  {
    return Collections
        .singletonList(new InputPortInfo(INPUT_FILE, "input_file"));
  }




  // @Override public List<String> getDefaultInputTypes() { return
  // Collections.singletonList("input_file"); }
  @Override
  public List<OutputPortInfo> getDefaultOutputs()
  {
    return Collections.singletonList(new OutputPortInfo("f"));
  }




  @Override
  public String getCategory()
  {
    return NodeCategories.TEXT_DATA;
  }




  public File getInputFile(Map<String, String> properties, RuntimeData runtime)
  {
    return getFileFromPortOrProperty(runtime,
        properties,
        INPUT_FILE,
        true,
        true);
  }




  public String getMatch(Map<String, String> properties)
  {
    return getRequiredProperty(properties, MATCH);
  }




  public int getField(Map<String, String> properties)
  {
    return getRequiredIntProperty(properties, FIELD);
  }




  public int getSkip(Map<String, String> properties)
  {
    int skip = 0;
    try {
      skip = Integer.parseInt(properties.get(SKIP));
    }
    catch (Exception ex) {
      // Fall back to default
    }
    return skip;
  }




  @Override
  public List<PropertyInfo> getDefaultProperties()
  {
    return Arrays.asList(new PropertyInfo(INPUT_FILE, "home_file"),
        new PropertyInfo(MATCH, "default"),
        new PropertyInfo(FIELD, "default"),
        new PropertyInfo(SKIP, "default"),
        new PropertyInfo(SEPARATOR, "default"));
  }
  // @Override public List<String> getDefaultProperties() { return
  // Arrays.asList(INPUT_FILE, MATCH, FIELD, SKIP); }
  // @Override public List<String> getDefaultPropertyTypes() { return
  // Arrays.asList("home_file", "default", "default", "default"); }




}
