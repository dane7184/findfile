

window.addEventListener("load",handleLoadApp)

async function openFolderPicker() {
    try {
        const directoryHandle = await window.showDirectoryPicker();
        console.log({directoryHandle});
    } catch (err) {
        console.error(err);
    }
}

async function handleChangeFolder(e) {
    
    const selectedFolder = e.target.files[0];
    
    console.log(selectedFolder);
    
}


function handleLoadApp(){

    const currentPath = location.pathname ;

    if(currentPath === "/") {
        document.querySelector(".app-menu-item.home").classList.add("menu-active");
        return ;
    }

    document.querySelector(`.app-menu-item.${currentPath.split('/')[1]}`)?.classList.add("menu-active")

}

// function handleDelete(event) {
//     const fileName = event.currentTarget?.getAttribute('aria-valuetext');
//     const isConfirm = confirm(`Are you sure that you want to delete ${fileName} ?`);
//     if(!isConfirm) event.preventDefault();
// }