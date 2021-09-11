;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns license.events
  (:require
   [re-frame.core :as re-frame]
   [license.db :as db]
   [ajax.core :as ajax]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [day8.re-frame.http-fx]))

(re-frame/reg-event-fx
 ::initialize-db
 (fn-traced [_ _]
            {:db db/default-db
             :http-xhrio {:method :get
                          :uri "https://api.github.com/licenses"
                          :format (ajax/json-request-format)
                          :response-format (ajax/json-response-format {:keywords? true})
                          :on-success [::process-licenses-response]}
             }))

(re-frame/reg-event-fx
 ::select-license
 (fn-traced [{db :db} [_ license]]
            (cond-> {}
              (not (contains? (get-in db [:licenses (keyword license)]) :text))
              (assoc :dispatch [::fetch-license license])
              true (assoc :db (assoc db :selected-license license)))))

(re-frame/reg-event-fx
 ::fetch-license
 (fn-traced [{db :db} [_ license]]

            {:http-xhrio {:method :get
                          :uri (str "https://api.github.com/licenses/" license)
                          :format (ajax/json-request-format)
                          :response-format (ajax/json-response-format {:keywords? true})
                          :on-success [::process-license-response]}
             :db (assoc db :loading? true)}))

(re-frame/reg-event-db
 ::toggle-theme-mode
 (fn-traced [db _]
            (let [theme-mode (:theme-mode db)]
              (assoc db :theme-mode (if (= theme-mode "dark") "light" "dark")))))

(re-frame/reg-event-db
 ::process-licenses-response
 (fn-traced [db [_ response]]
            (-> db
                (assoc :loading? false)
                (assoc :licenses
                       (reduce #(assoc %1 (keyword (:key %2)) %2) {} response)))))

(re-frame/reg-event-db
 ::process-license-response
 (fn-traced [db [_ response]]
            (let [text (:body response)]
              (-> db
                  (assoc :loading? false)
                  (assoc-in [:licenses (keyword (:key response)) :text] text)))))
