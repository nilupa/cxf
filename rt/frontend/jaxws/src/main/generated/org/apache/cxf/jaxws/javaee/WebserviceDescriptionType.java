//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.10.31 at 10:25:50 AM GMT+08:00 
//


package org.apache.cxf.jaxws.javaee;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 * 
 * 	The webservice-description element defines a WSDL document file
 * 	and the set of Port components associated with the WSDL ports
 * 	defined in the WSDL document.  There may be multiple
 * 	webservice-descriptions defined within a module.
 * 
 * 	All WSDL file ports must have a corresponding port-component element
 * 	defined.
 * 
 * 	Used in: webservices
 * 
 *       
 * 
 * <p>Java class for webservice-descriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="webservice-descriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://java.sun.com/xml/ns/javaee}descriptionType" minOccurs="0"/>
 *         &lt;element name="display-name" type="{http://java.sun.com/xml/ns/javaee}display-nameType" minOccurs="0"/>
 *         &lt;element name="icon" type="{http://java.sun.com/xml/ns/javaee}iconType" minOccurs="0"/>
 *         &lt;element name="webservice-description-name" type="{http://java.sun.com/xml/ns/javaee}string"/>
 *         &lt;element name="wsdl-file" type="{http://java.sun.com/xml/ns/javaee}pathType" minOccurs="0"/>
 *         &lt;element name="jaxrpc-mapping-file" type="{http://java.sun.com/xml/ns/javaee}pathType" minOccurs="0"/>
 *         &lt;element name="port-component" type="{http://java.sun.com/xml/ns/javaee}port-componentType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "webservice-descriptionType", propOrder = {
    "description",
    "displayName",
    "icon",
    "webserviceDescriptionName",
    "wsdlFile",
    "jaxrpcMappingFile",
    "portComponent"
})
public class WebserviceDescriptionType {

    protected DescriptionType description;
    @XmlElement(name = "display-name")
    protected DisplayNameType displayName;
    protected IconType icon;
    @XmlElement(name = "webservice-description-name", required = true)
    protected org.apache.cxf.jaxws.javaee.String webserviceDescriptionName;
    @XmlElement(name = "wsdl-file")
    protected PathType wsdlFile;
    @XmlElement(name = "jaxrpc-mapping-file")
    protected PathType jaxrpcMappingFile;
    @XmlElement(name = "port-component", required = true)
    protected List<PortComponentType> portComponent;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected java.lang.String id;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link DescriptionType }
     *     
     */
    public DescriptionType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescriptionType }
     *     
     */
    public void setDescription(DescriptionType value) {
        this.description = value;
    }

    /**
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link DisplayNameType }
     *     
     */
    public DisplayNameType getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link DisplayNameType }
     *     
     */
    public void setDisplayName(DisplayNameType value) {
        this.displayName = value;
    }

    /**
     * Gets the value of the icon property.
     * 
     * @return
     *     possible object is
     *     {@link IconType }
     *     
     */
    public IconType getIcon() {
        return icon;
    }

    /**
     * Sets the value of the icon property.
     * 
     * @param value
     *     allowed object is
     *     {@link IconType }
     *     
     */
    public void setIcon(IconType value) {
        this.icon = value;
    }

    /**
     * Gets the value of the webserviceDescriptionName property.
     * 
     * @return
     *     possible object is
     *     {@link org.apache.cxf.jaxws.javaee.String }
     *     
     */
    public org.apache.cxf.jaxws.javaee.String getWebserviceDescriptionName() {
        return webserviceDescriptionName;
    }

    /**
     * Sets the value of the webserviceDescriptionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.apache.cxf.jaxws.javaee.String }
     *     
     */
    public void setWebserviceDescriptionName(org.apache.cxf.jaxws.javaee.String value) {
        this.webserviceDescriptionName = value;
    }

    /**
     * Gets the value of the wsdlFile property.
     * 
     * @return
     *     possible object is
     *     {@link PathType }
     *     
     */
    public PathType getWsdlFile() {
        return wsdlFile;
    }

    /**
     * Sets the value of the wsdlFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link PathType }
     *     
     */
    public void setWsdlFile(PathType value) {
        this.wsdlFile = value;
    }

    /**
     * Gets the value of the jaxrpcMappingFile property.
     * 
     * @return
     *     possible object is
     *     {@link PathType }
     *     
     */
    public PathType getJaxrpcMappingFile() {
        return jaxrpcMappingFile;
    }

    /**
     * Sets the value of the jaxrpcMappingFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link PathType }
     *     
     */
    public void setJaxrpcMappingFile(PathType value) {
        this.jaxrpcMappingFile = value;
    }

    /**
     * Gets the value of the portComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the portComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPortComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PortComponentType }
     * 
     * 
     */
    public List<PortComponentType> getPortComponent() {
        if (portComponent == null) {
            portComponent = new ArrayList<PortComponentType>();
        }
        return this.portComponent;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setId(java.lang.String value) {
        this.id = value;
    }

}
