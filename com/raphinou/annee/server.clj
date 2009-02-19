(ns com.raphinou.annee.server
  (:use [ compojure.http helpers routes servlet]
        [ compojure.html form-helpers page-helpers]
        compojure.html
        compojure.server.jetty
        compojure.file-utils))

;import joda library
(import '(org.joda.time DateTime Days Weeks) '(org.joda.time.format DateTimeFormat))

; returns date of next new year relative to working-date
(defn new-year [d]  (.. d (plusYears 1) (withDayOfYear 1)))

; builds the webpage
(defn layout [ body ]
  [{"Content-Type" "text/html;charset=UTF-8"}  (html [:html
          [:body
            body
          ]
         ]
   )]
)

;displays a row of the table
(defn display-row [ title value]
  (html [ :tr [ :td.value value ":"][ :td.title title ]
         ]
   )
)

; formats the date (used in the title)
(defn format-date [d]
  (.. (DateTimeFormat/forPattern "dd/MM/yyy" ) (print d) )
)

; gets the day of year number
(defn day-of-year [ d ]
  (.. d dayOfYear getAsText)
)

; computes number of days to next new year relative to date passed
(defn days-to-new-year [d]
   (.. Days (daysBetween d (new-year d)) getDays)
)

; computes in which week of the year the passed date is
(defn week-of-year [d]
  (.. d weekOfWeekyear  get)
)
; computes weeks left until new year
(defn weeks-to-new-year [d]
   (.. Weeks (weeksBetween d (new-year d)) getWeeks)
)
; returns month name
(defn month-in-word [d]
  (.. d monthOfYear (getAsText (java.util.Locale/FRENCH)) )
)

; returns day of week name
(defn day-in-word [d]
  (.. d dayOfWeek (getAsText (java.util.Locale/FRENCH)) ))

; returns message stating if year is leap or not
(defn leap-message [d]
  (if (.. d year isLeap) "est une année bisextile" " n'est pas une année bisextile")
)

; returns year of the date passed in
(defn year [d]
  (.. d year get))


(defn default-view [ working-date ]
  "renders the default view"
  (layout 
   (html 
    [:h1 (format-date working-date ) ]
    [ :table
      (display-row (leap-message working-date) (year working-date)) 
      (display-row "Le nom du mois" (month-in-word working-date)) 
      (display-row "Le jour de la semaine" (day-in-word working-date)) 
      (display-row "Le jour de l'annee" (day-of-year working-date)) 
      (display-row "Nombre de jours restant cette année" (days-to-new-year working-date)) 
      (display-row "Semaine de l'année" (week-of-year working-date)) 
      (display-row "Semaines restant cette année" (weeks-to-new-year working-date)) 
    ]
    )
  )
)

(defservlet annee-servlet
  "Servlet to answer annee requests"
  ; current date by default
  (GET "/" (default-view (DateTime.)) )

  ; specify year
  (GET "/:year" (default-view (.. (DateTime.) (withYear (Integer/parseInt (route :year))))))

  ; specify year and month
  (GET "/:year/:month" (default-view (.. (DateTime.) 
     (withYear (Integer/parseInt (route :year))) 
     (withMonthOfYear (Integer/parseInt (route :month))))
  ))

  ; specify year month and day
  (GET "/:year/:month/:day" (default-view (.. (DateTime.) 
     (withYear (Integer/parseInt (route :year))) 
     (withMonthOfYear (Integer/parseInt (route :month)))
     (withDayOfMonth (Integer/parseInt (route :day)))))
  )
  (ANY "*" (page-not-found))
)

(defserver annee-server {:port 8080}
   "/*" annee-servlet)

; start server with:
;(compojure.server.jetty/start annee-server)
