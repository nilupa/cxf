package org.objectweb.celtix.tools.common.toolspec.parser;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.objectweb.celtix.common.logging.LogUtils;
import org.objectweb.celtix.tools.common.toolspec.Tool;

public class Option implements TokenConsumer {

    private static final Logger LOG = LogUtils.getL7dLogger(Option.class);

    protected Element argument;
    protected Element annotation;
    private final Element element;
    private Element valueType;

    private int numMatches;

    public Option(Element el) {
        this.element = el;

        NodeList list = element.getElementsByTagNameNS(Tool.TOOL_SPEC_PUBLIC_ID, "associatedArgument");

        if (list != null && list.getLength() > 0) {
            argument = (Element)list.item(0);
        }

        list = element.getElementsByTagNameNS(Tool.TOOL_SPEC_PUBLIC_ID, "annotation");
        if (list != null && list.getLength() > 0) {
            annotation = (Element)list.item(0);
        }

        if (annotation == null && argument != null) {
            list = argument.getElementsByTagNameNS(Tool.TOOL_SPEC_PUBLIC_ID, "annotation");
            if (list != null && list.getLength() > 0) {
                annotation = (Element)list.item(0);
            }
        }
    }

    public boolean hasArgument() {
        return argument != null;
    }

    public boolean hasImmediateArgument() {
        return hasArgument() && argument.getAttribute("placement").equals("immediate");
    }

    /**
     * @return whether the first token was accepted
     */
    public boolean accept(TokenInputStream args, Element result, ErrorVisitor errors) {

        if (args.available() == 0) {
            return false;
        }
        String arg = args.peek();

        if (arg == null) {
            LOG.severe("ARGUMENT_IS_NULL_MSG");
        }

        // go through each switch to see if we can match one to the arg.
        NodeList switches = element.getElementsByTagNameNS(Tool.TOOL_SPEC_PUBLIC_ID, "switch");

        boolean accepted = false;

        for (int i = 0; i < switches.getLength(); i++) {

            String switchArg = "-" + switches.item(i).getFirstChild().getNodeValue();
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("switchArg is " + switchArg);
            }
            if (hasImmediateArgument() ? arg.startsWith(switchArg) : arg.equals(switchArg)) {
                if (LOG.isLoggable(Level.INFO)) {
                    LOG.info("Matches a switch!!!");
                }
                // consume the token
                args.read();
                // Add ourselves to the result document
                Element optionEl = result.getOwnerDocument()
                    .createElementNS("http://www.xsume.com/Xutil/Command", "option");

                optionEl.setAttribute("name", getName());

                // Add argument value to result
                if (hasArgument()) {
                    String argValue;
                    if (hasImmediateArgument()) {
                        argValue = arg.substring(switchArg.length());
                    } else {
                        argValue = readArgumentValue(args, switchArg, errors);
                    }
                    if (argValue != null) {
                        if (LOG.isLoggable(Level.INFO)) {
                            LOG.info("Setting argument value of option to " + argValue);
                        }
                        optionEl.appendChild(result.getOwnerDocument().createTextNode(argValue));

                    } else {
                        break;
                    }
                }
                result.appendChild(optionEl);
                numMatches++;
                accepted = true;
            }
        }
        return accepted;
    }

    private String readArgumentValue(TokenInputStream args, String switchArg, ErrorVisitor errors) {
        String value = null;
        if (args.available() > 0) {
            value = args.read();
            if (value.startsWith("-")) {
                errors.add(new ErrorVisitor.InvalidOption(switchArg));
                value = null;
            } else if (hasInvalidCharacter(value)) {
                errors.add(new ErrorVisitor.UserError(switchArg + " has invalid character!"));
            }
        } else {
            errors.add(new ErrorVisitor.InvalidOption(switchArg));
        }
        return value;
    }

    private boolean hasInvalidCharacter(String argValue) {
        NodeList list = argument.getElementsByTagNameNS(Tool.TOOL_SPEC_PUBLIC_ID, "valuetype");
        String valuetypeStr = null;

        if (list != null && list.getLength() > 0) {
            valueType = (Element)list.item(0);
            valuetypeStr = valueType.getFirstChild().getNodeValue();

            if ("IdentifyString".equals(valuetypeStr)) {
                return !isIdentifyString(argValue);
            } else if ("Digital".equals(valuetypeStr)) {
                for (int i = 0; i < argValue.length(); i++) {
                    if (!Character.isDigit(argValue.charAt(i))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isIdentifyString(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '.') {
                continue;
            } else {
                if (!Character.isJavaIdentifierPart(value.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isSatisfied(ErrorVisitor errors) {
        if (errors.getErrors().size() > 0) {
            return false;
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("For this option, minOccurs=" + element.getAttribute("minOccurs") + " and maxOccurs="
                     + element.getAttribute("maxOccurs") + ", numMatches currently " + numMatches);
        }
        boolean result = true;

        if (!isAtleastMinimum()) {
            errors.add(new ErrorVisitor.MissingOption(this));
            result = false;
        }
        if (result && !isNoGreaterThanMaximum()) {
            errors.add(new ErrorVisitor.DuplicateOption(getName()));
            result = false;
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("isSatisfied() returning " + result);
        }
        return result;
    }

    private boolean isAtleastMinimum() {
        boolean result = true;
        int minOccurs = 0;

        if (!element.getAttribute("minOccurs").equals("")) {
            result = numMatches >= Integer.parseInt(element.getAttribute("minOccurs"));
        } else {
            result = numMatches >= minOccurs;
        }
        return result;
    }

    private boolean isNoGreaterThanMaximum() {
        boolean result = true;
        int maxOccurs = 1;

        if (!element.getAttribute("maxOccurs").equals("")) {
            result = element.getAttribute("maxOccurs").equals("unbounded")
                     || numMatches <= Integer.parseInt(element.getAttribute("maxOccurs"));
        } else {
            result = numMatches <= maxOccurs;
        }
        return result;
    }

    public String getName() {
        return element.getAttribute("id");
    }

    public String getAnnotation() {
        return annotation.getFirstChild().getNodeValue();
    }

    public String getPrimarySwitch() {
        NodeList switches = element.getElementsByTagNameNS(Tool.TOOL_SPEC_PUBLIC_ID, "switch");

        // options must have atleast one switch, as enforced by schema, so no
        // need for defensive coding.
        return switches.item(0).getFirstChild().getNodeValue();
    }

    public String toString() {
        return getName();
    }

}
