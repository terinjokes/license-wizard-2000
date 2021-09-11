;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns license.views
  (:require
   [re-frame.core :as re-frame]
   [license.subs :as subs]
   [license.events :as events]
   [reagent-material-ui.core.css-baseline :refer [css-baseline]]
   [reagent-material-ui.core.typography :refer [typography]]
   [reagent-material-ui.core.app-bar :refer [app-bar]]
   [reagent-material-ui.core.toolbar :refer [toolbar]]
   [reagent-material-ui.core.container :refer [container]]
   [reagent-material-ui.core.paper :refer [paper]]
   [reagent-material-ui.core.select :refer [select]]
   [reagent-material-ui.core.menu-item :refer [menu-item]]
   [reagent-material-ui.core.input-label :refer [input-label]]
   [reagent-material-ui.core.form-control :refer [form-control]]
   [reagent-material-ui.core.text-field :refer [text-field]]
   [reagent-material-ui.core.icon-button :refer [icon-button]]
   [reagent-material-ui.icons.brightness-4 :refer [brightness-4]]
   [reagent-material-ui.icons.brightness-7 :refer [brightness-7]]
   [reagent-material-ui.core.skeleton :refer [skeleton]]
   [reagent-material-ui.styles :as styles]))

(defn main-panel []
  (let [theme-mode @(re-frame/subscribe [::subs/theme-mode])
        loading? @(re-frame/subscribe [::subs/loading?])
        licenses @(re-frame/subscribe [::subs/licenses])
        selected-license @(re-frame/subscribe [::subs/selected-license])]
    [styles/theme-provider (styles/create-theme {:palette {:mode theme-mode}})
     [css-baseline]
     [app-bar {:position "static"}
      [toolbar
       [typography {:variant "h6" :component "div" :sx {:flexGrow 1}} "License Wizard 2000"]
       [icon-button {:sx {:ml 1}
                     :onClick #(re-frame/dispatch [::events/toggle-theme-mode])} (if (= theme-mode "dark") [brightness-4] [brightness-7])]]]
     [:div
      [container {:maxWidth "lg" :sx {:mt 4 :mb 4}}
       [paper {:sx {:p 2 :display "flex" :flexDirection "column"}}
        [form-control
         (cond
           loading? [:<>
                     [skeleton {:width "100%"} [select]]
                     [skeleton {:width "100%"} [text-field {:multiline true :rows 25}]]]
           :else
           [:<>
            [input-label {:id "license-select"} "Select a license"]
            [select {:label "Select a license" :labelId "license-select" :value selected-license
                     :sx {:mb 1}
                     :onChange #(re-frame/dispatch [::events/select-license (-> % .-target .-value)])}
             (for [[_ license] licenses] ^{:key (:key license)}
               [menu-item {:value (:key license)} (:name license)])]
            (when-let [text (get-in licenses [(keyword selected-license) :text] false)]
              [text-field {:label "License" :multiline true :value text}])
            ])]]]]]))
