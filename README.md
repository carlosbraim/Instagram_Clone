# Instagram_Clone


Este projeto contém informações para desenvolver um aplicativo onde o usuário consegue:
- Seguir amigos 
- Publicações em seu feed
- Comentar e curtir Fotos
- Adicionar filtro as imagens 
- Adicionar imagem da Galeria e da Câmera do seu dispositivo

Todas as funções do aplicativo como adicionar imagem em círculo estão comentadas, **lembrando que segue o arquivo Android.docx com mais 
informações e apoio no desenvolvimento do projeto.**




#### Dependências necessárias para o funcionamento do projeto
        dependencies {
            implementation fileTree(dir: 'libs', include: ['*.jar'])
            implementation 'com.android.support:appcompat-v7:26.1.0'
            implementation "com.android.support:design:26.+"
            implementation 'com.android.support.constraint:constraint-layout:1.1.3'
            implementation 'com.android.support:support-v4:26.1.0'
            testImplementation 'junit:junit:4.12'
            androidTestImplementation 'com.android.support.test:runner:1.0.2'
            androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'

            //Dependecias firebase
            implementation 'com.google.firebase:firebase-core:16.0.1'
            implementation 'com.google.firebase:firebase-database:16.0.1'
            implementation 'com.google.firebase:firebase-storage:16.0.1'
            implementation 'com.google.firebase:firebase-auth:16.0.1'
            implementation 'com.firebaseui:firebase-ui-storage:0.6.0'

            //Dependencias bottom navigationEx
            implementation 'com.github.ittianyu:BottomNavigationViewEx:1.2.4'

            //dependencia circle image view
            implementation 'de.hdodenhof:circleimageview:2.2.0'

            //dependencias AndroidPhotoFilter
            implementation 'info.androidhive:imagefilters:1.0.7'

            //dependencia da Lib Universal Image Loader
            implementation 'com.nostra13.universalimageloader:universal-image-loader:1.9.5'

            //dependencia like button
            implementation 'com.github.jd-alexander:LikeButton:0.2.3'

            implementation 'com.android.support:cardview-v7:26.1.0'


        }
        
        
### GitHub com as dependência detalhadas


#### Like Button
https://github.com/jd-alexander/LikeButton

#### Filtros para imagens
https://github.com/ravi8x/AndroidPhotoFilters

#### Material para pesquisa dentro do App
https://github.com/MiguelCatalan/MaterialSearchView

#### Imagens circular
https://github.com/hdodenhof/CircleImageView

#### Barra de navegação
https://github.com/ittianyu/BottomNavigationViewEx




#### Telas de Login e Cadastro
<img src="/Instagram/Prints_tela/Instagram1.png" width="150"> <img src="/Instagram/Prints_tela/Instagram2.png" width="150">

Esta etapa possui todas as validações de campos e suas tratavias com criptografia em base64.

#### Visualizar Posts
<img src="/Instagram/Prints_tela/Instagram3.png" width="150"> <img src="/Instagram/Prints_tela/Instagram4.png" width="150"> <img src="/Instagram/Prints_tela/Instagram5.png" width="150"> <img src="/Instagram/Prints_tela/Instagram6.png" width="150"> <img src="/Instagram/Prints_tela/Instagram7.png" width="150">




É possível visualizar as postagem de todos os amigos em que você segue, comentar e curtir as postagens. Além disso o usuário
pode navegar na barra inferior para pesquisar amigos, editando seu perfil, fazer publicações e usar filtros entre outras. Nesse projeto aprendemos tudo isso 
por meio dos códigos comentados e as bibliotecas citadas acima.
