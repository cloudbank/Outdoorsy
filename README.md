# Outdoorsy v1

  <img src="https://i.imgur.com/zCABetL.png" height="350"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://i.imgur.com/mjOD88u.png" height="350"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://i.imgur.com/x0o2a8E.png" height="350"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://i.imgur.com/Pal4ASq.png" height="350"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;


###   --Outdoorsy API app for searching RV rentals

Androidx, ICS(14) to Android10, latest best practices @ google

Offline first architecture, SSOT is the db, data is from the Outdoorsy API.
`MVVM` pattern with `Paging2`, `LiveData` and `Room`, and `Repository` pattern with Coroutines, Navigation are used to page in data
for the UI and also back-fill from the network as the user reaches the end of the list or LiveData detects a change.
Search text requests filtered results from the API while the pagedlist is also querying the db for name/description matches.  There is a unique index
on the id.(primary key API based id). No text gets all results.

`Room` uses a `DataSource.Factory` as a positional data source and the Paging Boundary Callback
API to get notified when the Paging library consumes the available local data.  NetworkState implementation
keeps track of network status.
Backdrop implementation is used to show results in a sheet that transitions with touch and navigation.  No menu is currently implemented but could
for canned queries or other features.


Cached content is always available on the device and the user will still have a good experience even if the network is slow /
unavailable ---> OFFLINE_MODE!
Glide caches images as long as they are initially loaded.


### Libraries
* [Androidx][]
* [Android Architecture Components][arch]
* [Coroutines][]
* [Retrofit][retrofit] for REST api communication
* [Glide][glide] for image loading
* [espresso][espresso] for UI tests
* [mockito][mockito] for mocking in tests
* [Retrofit Mock][retrofit-mock] for creating a fake API implementation for tests

[mockwebserver]: https://github.com/square/okhttp/tree/master/mockwebserver
[arch]: https://developer.android.com/arch
[espresso]: https://google.github.io/android-testing-support-library/docs/espresso/
[retrofit]: http://square.github.io/retrofit
[glide]: https://github.com/bumptech/glide
[mockito]: http://site.mockito.org
[retrofit-mock]: https://github.com/square/retrofit/tree/master/retrofit-mock
