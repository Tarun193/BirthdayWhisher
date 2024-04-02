/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const { onRequest } = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });


const functions = require('firebase-functions');
const admin = require('firebase-admin');
const twilio = require('twilio');

admin.initializeApp();


// Assuming environment variables are set in the Google Cloud Function configuration,
// not using dotenv here.
const client = twilio(functions.config().twilio.accountsid, functions.config().twilio.authtoken);


function wishBirthday(name, relationship) {
    switch (relationship) {
        case 'Friend':
            return `Hey 🎉 Wishing you a day filled with happiness, laughter, and all the wonderful things that you bring into this world. Happy Birthday, my friend! Let's make some unforgettable memories today. 🎂🎈`;
        case 'Office':
            return `Happy Birthday! 🎈 Your hard work and dedication are truly inspiring to us all. Wishing you success and happiness in the year to come. Let's celebrate your special day! 🍰🎉`;
        case 'Family':
            return `Happy Birthday, ${name}! 🎂 You mean the world to us, and we're so grateful for every moment with you. May your day be as wonderful as you are to us. Here's to more love, joy, and shared memories. ❤️🎁`;
        case 'Partner':
            return `Happy Birthday to the love of my life, ${name}! 💖 Every day with you is special, but today is extra special because it's your day. Wishing you all the love, joy, and happiness you deserve. Let's make today unforgettable. 🌹🎂`;
        default:
            return `Happy Birthday! Wishing you a fantastic day ahead. 🎉🎂`;
    }
}


async function getContactDetails(contactRef) {
    const doc = await contactRef.get();
    return doc.exists ? doc.data() : null;
}

async function sendSMS(contact, name) {
    message = contact.message.trim();
    if (!contact.message.trim()) {
        message = `Happy Birthday! Wishing you a fantastic day ahead. 🎉🎂`;
    }
    await client.messages
        .create({
            body: `Dear ${contact.name},\n${message}\n-${name}`,
            messagingServiceSid: 'MG459e42cc6b78d1b2dd1df62fefcccb6b',
            to: contact.phone,
        });
}


async function filterUsersWithBirthdayContacts(users) {
    const today = new Date();
    const day = today.getDate();
    const month = today.getMonth() + 1; // JavaScript months are 0-based

    // Users with at least one contact having a birthday today
    const usersToSendNotification = [];

    for (const user of users) {
        if (!user.contacts || user.contacts.length === 0 || !user.fcmToken) {
            continue; // Skip users without contacts or an FCM token
        }

        const messages = [];

        const contacts = user.contacts;
        for (const contactRef of contacts) {
            const contact = await getContactDetails(contactRef);
            if (contact) {
                const [dobDay, dobMonth] = contact.DOB.split('-').map(Number);
                if (dobDay === day && dobMonth === month) {
                    if (!contact.send) {
                        try {
                            console.log("Sending")
                            await sendSMS(contact, user.name);
                            await contactRef.update({ send: true })
                            messages.push({
                                title: "Birthday Alert!",
                                body: `${contact.name}, One of your contacts has a birthday today!`,
                            })
                        }
                        catch (e) {
                            console.log();
                        }
                    }
                } else {
                    await contactRef.update({ send: false })
                }
            }
        }
        user.messages = messages;
        usersToSendNotification.push(user);
    }

    return usersToSendNotification;
}

async function sendFCMToUsersWithBirthdayContacts() {
    const users = [];
    const usersData = await admin.firestore().collection("Users").get();
    usersData.forEach(item => {
        users.push(item.data());
    })
    const usersWithBirthdayContacts = await filterUsersWithBirthdayContacts(users);



    for (const user of usersWithBirthdayContacts) {
        for (const msg of user.messages) {
            const message = {
                token: user.fcmToken,
                notification: {
                    title: msg.title,
                    body: msg.body,
                },
            };

            try {
                const response = await admin.messaging().send(message);
                console.log(`Successfully sent message: ${response}`);
            } catch (error) {
                console.error(`Error sending message: ${error}`);
            }
        }
    }
}

exports.sendBirthdayNotifications = functions.https.onRequest(async (req, res) => {
    try {
        await sendFCMToUsersWithBirthdayContacts();
        res.status(200).send('Notifications sent successfully.');
    } catch (error) {
        console.error('Error sending notifications:', error);
        res.status(500).send('Error sending notifications');
    }
});

// Modify the scheduled function to call performDatabaseUpdate
exports.scheduledDailyTask = functions.pubsub.schedule('0 0 * * *')
    .timeZone('America/New_York') // Change to your time zone
    .onRun(async (context) => {
        console.log('This will run daily at 12 A.M. New York time!');
        await sendFCMToUsersWithBirthdayContacts();
        return null;
    });