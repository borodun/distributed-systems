
package hash.cracker.worker.types;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="PartNumber" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="PartCount" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="Hash" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="MaxLength" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="Alphabet"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="symbols" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "requestId",
    "partNumber",
    "partCount",
    "hash",
    "maxLength",
    "alphabet"
})
@XmlRootElement(name = "CrackHashManagerRequest", namespace = "http://ccfit.nsu.ru/schema/crack-hash-request")
public class CrackHashManagerRequest {

    @XmlElement(name = "RequestId", namespace = "http://ccfit.nsu.ru/schema/crack-hash-request", required = true)
    protected String requestId;
    @XmlElement(name = "PartNumber", namespace = "http://ccfit.nsu.ru/schema/crack-hash-request")
    protected int partNumber;
    @XmlElement(name = "PartCount", namespace = "http://ccfit.nsu.ru/schema/crack-hash-request")
    protected int partCount;
    @XmlElement(name = "Hash", namespace = "http://ccfit.nsu.ru/schema/crack-hash-request", required = true)
    protected String hash;
    @XmlElement(name = "MaxLength", namespace = "http://ccfit.nsu.ru/schema/crack-hash-request")
    protected int maxLength;
    @XmlElement(name = "Alphabet", namespace = "http://ccfit.nsu.ru/schema/crack-hash-request", required = true)
    protected CrackHashManagerRequest.Alphabet alphabet;

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the partNumber property.
     * 
     */
    public int getPartNumber() {
        return partNumber;
    }

    /**
     * Sets the value of the partNumber property.
     * 
     */
    public void setPartNumber(int value) {
        this.partNumber = value;
    }

    /**
     * Gets the value of the partCount property.
     * 
     */
    public int getPartCount() {
        return partCount;
    }

    /**
     * Sets the value of the partCount property.
     * 
     */
    public void setPartCount(int value) {
        this.partCount = value;
    }

    /**
     * Gets the value of the hash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHash(String value) {
        this.hash = value;
    }

    /**
     * Gets the value of the maxLength property.
     * 
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the value of the maxLength property.
     * 
     */
    public void setMaxLength(int value) {
        this.maxLength = value;
    }

    /**
     * Gets the value of the alphabet property.
     * 
     * @return
     *     possible object is
     *     {@link CrackHashManagerRequest.Alphabet }
     *     
     */
    public CrackHashManagerRequest.Alphabet getAlphabet() {
        return alphabet;
    }

    /**
     * Sets the value of the alphabet property.
     * 
     * @param value
     *     allowed object is
     *     {@link CrackHashManagerRequest.Alphabet }
     *     
     */
    public void setAlphabet(CrackHashManagerRequest.Alphabet value) {
        this.alphabet = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="symbols" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "symbols"
    })
    public static class Alphabet {

        @XmlElement(namespace = "http://ccfit.nsu.ru/schema/crack-hash-request")
        protected List<String> symbols;

        /**
         * Gets the value of the symbols property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the symbols property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSymbols().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getSymbols() {
            if (symbols == null) {
                symbols = new ArrayList<String>();
            }
            return this.symbols;
        }

    }

}
