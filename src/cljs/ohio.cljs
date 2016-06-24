(ns ohio
  (:require [strokes :refer [d3]]))

(strokes/bootstrap)

(def width 1200)
(def height 600)

(def svg (-> d3 (.select "#map") (.append "svg")
    (.attr {:width width
            :height height})))

(def draw-root (-> svg (.append "g")))

(def proj-fn (-> d3 .-geo .albersUsa (.scale 2000)
                 (.translate [ (/ width 9) (/ height 2)])))
(def path-fn (-> d3 .-geo .path (.projection proj-fn)))

(defn render [maproot]
  (-> draw-root
      (.append "g")
      (.attr "id" "states")
      (.selectAll "path")
      (.data (aget maproot "features"))
      (.enter)
      (.append "path")      
      (.attr "d" path-fn)
      (.style "fill", (fn [d]
                        (if-let [fill (.-FILL
                                       (.-properties d))]
                          fill)))))


(-> d3 (.json "ohiocounties.geojson" (fn [error1, maproot]
                                       (if-let [error error1]
                                         (-> (.html (aget error "response")))
                                         (render maproot)))))
