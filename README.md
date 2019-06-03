# android_exam_kea_bank
Exam project for 4th semester Android App Development

## My Solution
### Understanding
I chose in my solution to implement "accounts" under Customer, so that a single login would give the customer access to all his/her accounts at once, and still provide
an option to add/apply for more accounts in-app.
All the account information is stored on the backend that is oftentimes received, changed or created by the Android App by creating new accounts, sending or receiving money or changing/reset of a password.

### Backend
In my solution I opted for a REST API as a backend as this would allow me to store and handle data more efficiently.
The solution uses RestTemplates to GET, PUT and POST to the backend and at first on login parses a whole customer object to the OverviewActivity, where it later opts to GET whole customer objects per Activity.

### Reflections
There was an attempt at modularizing the program, however much of the code still is not modularized and was (in the case of Dialog Modules) difficult when trying to parse data.
Using a REST API was not the best decision as it took away a lot of my time by shifting my focus on how to interact with it as opposed to a simpler solution with more freedom to focus on Android.

## Getting it to work
To get this to work you must run the appropriate backend for it, which in this case can be found at: https://github.com/redrocket94/restapi
and to get this to work you must have access to a MySQL server and create a database named "androidapp" (note the default username and password set for the backend is username "root" and NO password.)

### Problems you might face
Anything to do with resetting the password may not work as the app tries to log onto a GMail account and GMail has a tendency to block unknown IPs.
In this case you can enter the source code and set the mail account to your own in the *Config* file in the *MailHandler* directory (remember to enable "Less Secure Apps" on your GMail for this to work).
