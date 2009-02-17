(ns com.raphinou.annee.server
  (:use [ compojure.http helpers routes servlet]
        [ compojure.html form-helpers page-helpers]
        compojure.html
        compojure.server.jetty
        compojure.file-utils))

(import '(org.joda.time DateTime Days Weeks) '(org.joda.time.format DateTimeFormat))

(def now (DateTime.))
(def new-year (.. now (plusYears 1) (withDayOfYear 1)))

(defn layout [ body ]
  (html [:html
          [:body
            body
          ]
         ]
   )
)
(defn display-row [ title value]
  (html [ :tr [ :td.value value ":"][ :td.title title ]
         ]
   )
)

(defn format-date [d]
  (.. (DateTimeFormat/forPattern "dd/MM/yyy" ) (print d) )
)

(defn day-of-year [ d ]
  (.. d dayOfYear getAsText)
)

(defn days-to-new-year [d]
   (.. Days (daysBetween now new-year) getDays)
)

;this doesn't work
(defn week-of-year [d]
  (.. d weekOfWeekyear  get)
)
(defn weeks-to-new-year [d]
   (.. Weeks (weeksBetween now new-year) getWeeks)
)
(defn month-in-word [d]
  (.. d monthOfYear (getAsText (java.util.Locale/FRENCH)) )
)

(defn day-in-word [d]
  (.. d dayOfWeek (getAsText (java.util.Locale/FRENCH)) ))

(defn leap-message [d]
  (if (.. d year isLeap) "est une année bisextile" " n'est pas une année bisextile")
)

(defn year [d]
  (.. d year get))


(defn default-view []
  "renders the default view"
  (layout 
   (html 
    [:h1 (format-date now ) ]
    [ :table
      (display-row (leap-message now) (year now)) 
      (display-row "Le nom du mois" (month-in-word now)) 
      (display-row "Le jour de la semaine" (day-in-word now)) 
      (display-row "Le jour de l'annee" (day-of-year now)) 
      (display-row "Nombre de jours restant cette année" (days-to-new-year now)) 
      (display-row "Semaine de l'année" (week-of-year now)) 
      (display-row "Semaines restant cette année" (weeks-to-new-year now)) 
    ]
    )
  )
)

(defservlet annee-servlet
  "Servlet to answer annee requests"
  (GET "/" (default-view) )
  (ANY "*" (page-not-found))
)

(defserver annee-server {:port 8080}
   "/*" annee-servlet)

; start server with:
;(compojure.server.jetty/start annee-server)
