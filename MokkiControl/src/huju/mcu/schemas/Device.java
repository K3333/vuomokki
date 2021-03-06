//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.20 at 12:11:23 ap. EEST 
//


package huju.mcu.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Device complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Device">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="deviceId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mcuBoardId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mcuBus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="deviceInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DisplayData" type="{http://vuomokki.dyndns.tv/mcuschemas}DisplayData" minOccurs="0"/>
 *         &lt;element name="DataUpdate" type="{http://vuomokki.dyndns.tv/mcuschemas}DataUpdate" minOccurs="0"/>
 *         &lt;element name="DataFilter" type="{http://vuomokki.dyndns.tv/mcuschemas}DataFilter" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Device", propOrder = {
    "type",
    "deviceId",
    "mcuBoardId",
    "mcuBus",
    "deviceInfo",
    "description",
    "displayData",
    "dataUpdate",
    "dataFilter"
})
public class Device {

    @XmlElement(required = true)
    protected String type;
    @XmlElement(required = true)
    protected String deviceId;
    @XmlElement(required = true)
    protected String mcuBoardId;
    @XmlElement(required = true)
    protected String mcuBus;
    protected String deviceInfo;
    protected String description;
    @XmlElement(name = "DisplayData")
    protected DisplayData displayData;
    @XmlElement(name = "DataUpdate")
    protected DataUpdate dataUpdate;
    @XmlElement(name = "DataFilter")
    protected DataFilter dataFilter;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the deviceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the value of the deviceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceId(String value) {
        this.deviceId = value;
    }

    /**
     * Gets the value of the mcuBoardId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMcuBoardId() {
        return mcuBoardId;
    }

    /**
     * Sets the value of the mcuBoardId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMcuBoardId(String value) {
        this.mcuBoardId = value;
    }

    /**
     * Gets the value of the mcuBus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMcuBus() {
        return mcuBus;
    }

    /**
     * Sets the value of the mcuBus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMcuBus(String value) {
        this.mcuBus = value;
    }

    /**
     * Gets the value of the deviceInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceInfo() {
        return deviceInfo;
    }

    /**
     * Sets the value of the deviceInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceInfo(String value) {
        this.deviceInfo = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the displayData property.
     * 
     * @return
     *     possible object is
     *     {@link DisplayData }
     *     
     */
    public DisplayData getDisplayData() {
        return displayData;
    }

    /**
     * Sets the value of the displayData property.
     * 
     * @param value
     *     allowed object is
     *     {@link DisplayData }
     *     
     */
    public void setDisplayData(DisplayData value) {
        this.displayData = value;
    }

    /**
     * Gets the value of the dataUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link DataUpdate }
     *     
     */
    public DataUpdate getDataUpdate() {
        return dataUpdate;
    }

    /**
     * Sets the value of the dataUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataUpdate }
     *     
     */
    public void setDataUpdate(DataUpdate value) {
        this.dataUpdate = value;
    }

    /**
     * Gets the value of the dataFilter property.
     * 
     * @return
     *     possible object is
     *     {@link DataFilter }
     *     
     */
    public DataFilter getDataFilter() {
        return dataFilter;
    }

    /**
     * Sets the value of the dataFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataFilter }
     *     
     */
    public void setDataFilter(DataFilter value) {
        this.dataFilter = value;
    }

}
