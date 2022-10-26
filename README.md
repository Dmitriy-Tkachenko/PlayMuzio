# Стриминговое приложение для воспроиведения музыки.
## На главной странице приложения находятся три раздела:
1. Featured Playlists - список рекомендуемых плейлистов (обновляется несколько раз в день).
2. New Releases - список самых актуальных новых релизов (обновляются каждую пятницу).
3. May Like - музыка, которая преимущественно может заинтересовать пользователя (обновляется при входе в приложение).

## Раздел Featured Playlists
В разделе Featured Playlists каждый выбранный плейлист содержит до 100 треков.
На следующих анимациях можно увидеть раздел Featured Playlists и пример работы:
![FeaturedPlaylists](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/FeaturedPlaylists.gif)
![Playlist1](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/FeaturedPlaylists1.gif)
![Playlist2](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/FeaturedPlaylists2.gif)

## Раздел New Releases
В разделе New Releases каждый новый релиз - это альбом, состоящий от 1 до N количества треков.
На следующих анимациях можно увидеть раздел New Releases и пример работы:
![NewReleases](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/NewReleases.gif)
![Album1](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/Album1.gif)
![Album2](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/Album2.gif)

## Раздел May Like
В разделе May Like на основе рассчитанной близости подбираются лучшие исполнители и треки, которые слушал пользователь. На основе полученных данных и избранных жанрах подбираются треки, которые преимещуственно могут понравиться пользователю.
На следующих анимациях можно увидеть раздел May Like с подборкой рекомендуемых треков и пример работы:
![May Like](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/MayLike.gif)
![Track1](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/Track1.gif)
![Track2](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/Track2.gif)
![Track3](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/Track3.gif)

## Экран прослушивания трека:
![Экран прослушивания трека](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/TrackPlay.png)

## Текст песни:
![Текст песни](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/TrackLyrics.png)

## Приложение работает как в портретной, так и в альбомной ориентации.
Пример экрана прослушивания трека в альбомной ориентации:
![Экран прослушивания трека в land](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/TrackPlayLand.png)

## Нотификация:
![Нотификация](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/Notification.png)

## Экран блокировки:
![Экран блокировки](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/LockScreen.png)

## Кэширование
Первый запрос (до кэширования):
![До кэширования](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/Caching1.gif)
Второй запрос (после кэширования):
![После кэширования](https://github.com/Dmitriy-Tkachenko/PlayMuzio/blob/master/readme-images/Caching2.gif)

## Дальнейшие ближайшие планы на разработку:
1. Добавление раздела "Похожие альбомы" на экран выбранного альбома
2. Добавление экрана артиста
3. Добавление раздела "Похожие артисты" на экран артиста
4. Добавление экрана "Моя музыка"
5. Добавление возможности сохранять треки и альбомы в раздел "Моя музыка"
6. Добавление экрана "Мои подписки"
7. Добавление возможности подписываться на артиста
8. Добавление возможности поиска музыки

## Используемые технологии:
1. Kotlin
2. Android SDK
3. Google Architecture Components
4. MVVM Architecture
5. Api Spotify
6. Api Musixmatch
7. ExoPlayer
8. Retrofit
9. Moshi
10. Glide
11. OkHTTP
12. Kotlin Coroutines
13. Transformations
14. CoordinatorLayout
