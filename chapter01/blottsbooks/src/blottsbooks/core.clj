(ns blottsbooks.core
    (:gen-class))

(defn say-welcome [what]
    (println "Welcome to" what "!"))

(defn -main []
    (say-welcome "Blotts Books"))
