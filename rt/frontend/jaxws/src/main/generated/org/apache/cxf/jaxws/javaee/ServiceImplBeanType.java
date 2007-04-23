//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.10.31 at 10:25:50 AM GMT+08:00 
//


package org.apache.cxf.jaxws.javaee;

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
 * 	The service-impl-bean element defines the web service implementation.
 * 	A service implementation can be an EJB bean class or JAX-RPC web
 * 	component.  Existing EJB implementations are exposed as a web service
 * 	using an ejb-link.
 * 
 * 	Used in: port-component
 * 
 *       
 * 
 * <p>Java class for service-impl-beanType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="service-impl-beanType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="ejb-link" type="{http://java.sun.com/xml/ns/javaee}ejb-linkType"/>
 *         &lt;element name="servlet-link" type="{http://java.sun.com/xml/ns/javaee}servlet-linkType"/>
 *       &lt;/choice>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "service-impl-beanType", propOrder = {
    "ejbLink",
    "servletLink"
})
public class ServiceImplBeanType {

    @XmlElement(name = "ejb-link")
    protected EjbLinkType ejbLink;
    @XmlElement(name = "servlet-link")
    protected ServletLinkType servletLink;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected java.lang.String id;

    /**
     * Gets the value of the ejbLink property.
     * 
     * @return
     *     possible object is
     *     {@link EjbLinkType }
     *     
     */
    public EjbLinkType getEjbLink() {
        return ejbLink;
    }

    /**
     * Sets the value of the ejbLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link EjbLinkType }
     *     
     */
    public void setEjbLink(EjbLinkType value) {
        this.ejbLink = value;
    }

    /**
     * Gets the value of the servletLink property.
     * 
     * @return
     *     possible object is
     *     {@link ServletLinkType }
     *     
     */
    public ServletLinkType getServletLink() {
        return servletLink;
    }

    /**
     * Sets the value of the servletLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServletLinkType }
     *     
     */
    public void setServletLink(ServletLinkType value) {
        this.servletLink = value;
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
