<h1 align="center">
  PocketDB
</h1>

<p align="center">
  <img src="https://images.unsplash.com/photo-1560748526-881455a4e9b2?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=850&q=80"/>
</p>

<p align="center">
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"></a>
  <a href="https://bintray.com/kucingapes/utsman/com.utsman.pocket"><img alt="Bintray" src="https://api.bintray.com/packages/kucingapes/utsman/com.utsman.pocket/images/download.svg"></a>
  <a href="https://github.com/utsmannn/pocketdb/pulls"><img alt="Pull request" src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat"></a>
  <a href="https://developer.android.com/kotlin"><img alt="Fcm docs" src="https://img.shields.io/badge/Kotlin-Coroutine-orange?logo=kotlin&style=flat"></a>
  <a href="https://twitter.com/utsmannn"><img alt="Twitter" src="https://img.shields.io/twitter/follow/utsmannn"></a>
  <a href="https://github.com/utsmannn"><img alt="Github" src="https://img.shields.io/github/followers/utsmannn?label=follow&style=social"></a>
  <h3 align="center">Android SharedPreferences Helper</h3>
</p>

---

This is SharedPreferences Helper like a database noSql. Support AES encryption

## Latest Version
[ ![Download](https://api.bintray.com/packages/kucingapes/utsman/com.utsman.pocket/images/download.svg) ](https://bintray.com/kucingapes/utsman/com.utsman.pocket/_latestVersion)

## Download
```groovy
dependencies {
    // required coroutine
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.8'

    // add this library
    implementation "com.utsman.pocket:pocketdb:$latest-version"
}
```

## Use
### Installation
This libray using Koin for Dependencies Injection, you need add this line in Application
```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Pocket.init(this, "utsmangantenkyah") // 16 digit secure key
    }
}
```

For existing Koin, you can install koin module after inject context `androidContext`
```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication) // required context injection
            Pocket.installKoinModule(this, "utsmangantenkyah") // 16 digit secure key
        }
    }
}
```

### Inserting
Pocket have two type class for inserting, `Row` and `Collection`. `Row` for store single data class apart from collection class, and the `Collection` for storing any collection class.

### Row
I want to insert this data
```kotlin
data class User(val name: String)

val user = User("utsman")
```

So I inserting it like this
```kotlin
Pocket.row("user").insert("utsman", user)
```

For retrieve that data, I need default of value, so add default param with `defaultOf(data)`
```kotlin
val default = defaultOf(User("sarah")) // default

// retrieve key of "utsman" from row of "user"
val data = Pocket.row("user").selectOf("utsman", default)
```

For need observe changes data, I use `flowOf` and call from coroutine scope or use `.listenOnUi` for observing in ui thread
```kotlin
Pocket.row("user")
    .flowOf("utsman", default)
    .listenOnUi { data ->
         // observing data changes here
     }

// or
GlobalScope.launch {
    Pocket.row("user")
        .flowOf("key", default)
        .collect {
            // observing data changes here
        }
}
```

And I want to remove data row of "utsman"
```kotlin
Pocket.row("user").destroy("utsman")
```

Or remove all data in row of "user"
```kotlin
Pocket.row("user").destroy()
```

### Collection
For the collection, has similiar function with row. Use `defaultCollectionOf()` for default data.
```kotlin
Pocket.collection("users")...
```

---

### Table of function
| Function | Row | Collection |
| --- | --- | --- |
| `insert` | yes | yes |
| `insertAll` | no | yes |
| `flowOf` | yes | yes |
| `selectOf` | yes | yes |
| `destroy` | yes | yes |
| `keys` | yes | yes |

```
Copyright 2020 Muhammad Utsman

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```