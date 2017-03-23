(ns fb-messenger.send
  (:require
    [org.httpkit.client :as http]))

(def ^:dynamic *base-url* "https://graph.facebook.com/v2.6")
(def ^:dynamic *page-access-token* nil)

(defn set-base-url!
  [base-url]
  (alter-var-root #'*base-url* (constantly base-url)))

(defn set-token!
  [token]
  (alter-var-root #'*page-access-token* (constantly token)))

(defn- post-api
  [endpoint body page-access-token]
  (let [response @(http/post (str *base-url* "/" endpoint)
                             {:query-params {"access_token" page-access-token}
                              :headers      {"Content-Type" "application/json"}
                              :body         (json/write-str body)
                              :insecure?    true})]
    (handle-facebook-response response)))

(defn send-message
  "sends Message to PSID via FB Send Api, see:
   https://developers.facebook.com/docs/messenger-platform/send-api-reference"
  [psid message]
  (send-message psid *page-access-token*)
  [psid message page-access-token]
  (post-api "me/messages" {:recipient {:id psid}
                           :message   message}))

(defn send-sender-action
  "send Sender Action to PSID via FB Send Api, see:
   https://developers.facebook.com/docs/messenger-platform/send-api-reference/sender-actions"
  [psid sender-action]
  (send-sender-action *page-access-token*)
  [psid sender-action page-access-token]
  (post-api "me/messages" {:recipient     {:id psid}
                           :sender_action sender-action}))

(defn upload-attachment
  "uploads Attachment via FB Upload API and returns body containing :attachment_id, see:
   https://developers.facebook.com/docs/messenger-platform/send-api-reference/attachment-upload/v2.8"
  [type url]
  (upload-attachment type url *page-access-token*)
  [type url page-access-token]
  (post-api "me/message_attachments"
            {:message {:attachment {:type    type
                                    :payload {:url         url
                                              :is_reusable true}}}}))

(defn set-messenger-profile
  "sets messenger profile data, see:
   https://developers.facebook.com/docs/messenger-platform/messenger-profile"
  [profile]
  (set-messenger-profile profile *page-access-token*)
  [profile page-access-token]
  (post-api "me/messenger_profile" profile))


(defn get-user-profile
  "gets user profile data for PSID, see:
   https://developers.facebook.com/docs/messenger-platform/user-profile"
  [psid]
  (get-user-profile psid *page-access-token*)
  [psid page-access-token]
  (let [response @(http/get (str *base-url* "/" psid)
                            {:query-params {:access_token page-access-token
                                            :fields       "first_name,last_name,profile_pic,locale,timezone,gender"}}
                            :headers       {"Content-Type" "application/json"}
                            :insecure?     true)]
    (handle-facebook-response response)))
