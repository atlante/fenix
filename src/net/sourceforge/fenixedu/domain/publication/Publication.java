/*
 * Created on Mar 29, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package net.sourceforge.fenixedu.domain.publication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Servico.ExcepcaoInexistente;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

/**
 * @author TJBF & PFON
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class Publication extends Publication_Base implements IPublication {

   

    public Publication() {
        super();
        setPublicationAuthors(new ArrayList());
        setPublicationTeachers(new ArrayList());
    } 

    public String toString() {
        String publication;
        publication = "";

        /*
         * if (getType() != null) { if (getType() .getPublicationType()
         * .equalsIgnoreCase("Outra Publicacao")) { publication =
         * getPublicationType().toUpperCase() + " : '\n'"; } else { publication =
         * getType().getPublicationType().toUpperCase() + " : '\n'"; } }
         */

        publication += getTitle();

        String str = "";
        if (getType()!=null && getType().getPublicationType()!=null && getType().getPublicationType().equalsIgnoreCase("translation"))
            str = " Translation";
        else if (getType()!=null && getType().getPublicationType()!=null && getType().getPublicationType().equalsIgnoreCase("critique"))
            str = " Critique";

        if (getSubType() != null && getSubType().length() != 0) {
            publication = publication + " (" + getSubType() + str + ")";
        }

        publication += " - ";

        Iterator iteratorAuthors = this.getAuthors().iterator();
        while (iteratorAuthors.hasNext()) {
            IAuthor author = (IAuthor) iteratorAuthors.next();

            if (author.getKeyPerson() != null && author.getKeyPerson().intValue() != 0) {
            	if (author.getPerson() == null)
            		publication += "NULL!, ";
            	else
            		publication += author.getPerson().getNome()+", ";
            } else {
                publication += author.getAuthor() + ", ";
            }
            //publication += ", ";
        }

        if (getJournalName() != null && getJournalName().length() != 0) {
            publication = publication + ", " + getJournalName();
        }

        if (getCriticizedAuthor() != null && getCriticizedAuthor().length() != 0) {
            publication = publication + ", Original Author: " + getCriticizedAuthor();
        }

        if (getConference() != null && getConference().length() != 0) {
            publication = publication + ", " + getConference();
        }

        if (getOriginalLanguage() != null && getOriginalLanguage().length() != 0
                && getLanguage() != null && getLanguage().length() != 0) {
            publication = publication + ", Translation " + getOriginalLanguage() + " - " + getLanguage();
        }

        if (getTranslatedAuthor() != null && getTranslatedAuthor().length() != 0) {
            publication = publication + ", Original Author: " + getTranslatedAuthor();
        }

        String ola = null;

        if (getEdition() != null) {
            switch (getEdition().intValue()) {
            case 1:
                ola = "st";
                break;
            case 2:
                ola = "nd";
                break;
            case 3:
                ola = "rd";
                break;
            default:
                ola = "th";
                break;
            }
        }

        if (getEdition() != null && getEdition().intValue() != 0) {
            publication = publication + " " + getEdition() + ola + " Edition";
        }

        if (getFascicle() != null && getFascicle().intValue() != 0) {
            publication = publication + ", Fasc. " + getFascicle();
        }

        if (getNumber() != null && getNumber().intValue() != 0) {
            publication = publication + ", No. " + getNumber();
        }

        if (getVolume() != null && getVolume().length() != 0) {
            publication = publication + ", Vol. " + getVolume();
        }

        if (getFirstPage() != null && getFirstPage().intValue() != 0) {
            publication = publication + " (Pag. " + getFirstPage();
        }

        if (getLastPage() != null && getLastPage().intValue() != 0) {
            publication = publication + " - " + getLastPage() + ")";
        }

        /*
         * if (getNumberPages() != null && getNumberPages().intValue() != 0) {
         * publication = publication + ", Number Pages = " + getNumberPages(); }
         */

        if (getSerie() != null && getSerie().intValue() != 0) {
            publication = publication + ", Serie " + getSerie();
        }

        if (getEditor() != null && getEditor().length() != 0) {
            publication = publication + ", " + getEditor();
        }

        /*
         * if (getIssn() != null && getIssn().intValue() != 0) { publication =
         * publication + ", ISSN = " + getIssn(); }
         * 
         * if (getIsbn() != null && getIsbn().intValue() != 0) { publication =
         * publication + ", ISBN = " + getIsbn(); }
         */

        /*
         * if (getScope() != null && getScope().length() != 0) { publication =
         * publication + ", Scope : " + getScope(); }
         */

        if (getLocal() != null && getLocal().length() != 0) {
            publication = publication + ", " + getLocal();
        }

        if (getUniversity() != null && getUniversity().length() != 0) {
            publication = publication + ", " + getUniversity();
        }

        if (getInstituition() != null && getInstituition().length() != 0) {
            publication = publication + ", " + getInstituition();
        }

        if (getMonth() != null && getMonth().length() != 0) {
            publication = publication + " " + getMonth();
        }

        if (getYear() != null && getYear().intValue() != 0) {
            publication = publication + " " + getYear();
        }

        if (getMonth_end() != null && getMonth_end().length() != 0) {
            publication = publication + " a " + getMonth_end();
        }

        if (getYear_end() != null && getYear_end().intValue() != 0) {
            publication = publication + " " + getYear_end();
        }

        if (getEditorCity() != null && getEditorCity().length() != 0) {
            publication = publication + " " + getEditorCity();
        }

        if (getCountry() != null && getCountry().length() != 0) {
            publication = publication + ", " + getCountry();
        }

        /*
         * if (getObservation() != null && getObservation().length() != 0) {
         * publication = publication + ", Observation : " + getObservation(); }
         */
        //publication = publication + ".";
        if (getFormat() != null && getFormat().length() != 0) {
            publication = publication + ", (" + getFormat() + " format)";
        }

        if (getUrl() != null && getUrl().length() != 0) {
            publication = publication + " " + getUrl();
        }

        return publication;
    }

    /**
     * @return a list of IAuthors
     */
    public List getAuthors(){
        List result = new ArrayList(getPublicationAuthors());
        Collections.sort(result, new BeanComparator("order"));

        List authors = (List) CollectionUtils.collect(result, new Transformer() {
            public Object transform(Object obj){
                IPublicationAuthor pa = (PublicationAuthor) obj;
                return pa.getAuthor();
            }
        });
        return authors;
    }
    
    /**
     * Sets the PublicationAuthors List 
     * @param a list of IAuthors
     */
    public void setAuthors(List authors){
       
        //List pubAuthors = new ArrayList();
    	getPublicationAuthors().removeAll(getPublicationAuthors());
        Iterator it = authors.iterator();
        int i = 0;
        while (it.hasNext()){
            i++;
            IAuthor author = (IAuthor) it.next();
            IPublicationAuthor pa = new PublicationAuthor();
            pa.setAuthor(author);
            pa.setPublication(this);
            pa.setOrder(new Integer(i));
            //pubAuthors.add(pa);
            getPublicationAuthors().add(pa);
        }
        //publicationAuthors = pubAuthors;
    }
    
    
    public Integer getOrderForAuthor(IAuthor author) throws ExcepcaoInexistente{
        List publicationAuthors = getPublicationAuthors();
        if (publicationAuthors == null || publicationAuthors.isEmpty())
            throw new ExcepcaoInexistente ("The author is not an author of this publication");
        Iterator it = publicationAuthors.iterator();
        int i = 0;
        while(it.hasNext()){
            i++;
            IPublicationAuthor pa = (IPublicationAuthor) it.next();
            if(pa.getAuthor().equals(author))
                return new Integer(i);
        }
        throw new ExcepcaoInexistente ("The author is not an author of this publication");
    }
    

}