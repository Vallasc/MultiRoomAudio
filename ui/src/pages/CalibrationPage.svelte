<script>
    import { Page, Navbar, Fab, Icon, List, ListInput, 
              BlockTitle, Block, Button, ListItem } from 'framework7-svelte';
    
    let isLoading = false;

    async function saveReference(){
      console.log(await JSInterface.saveReferencePoint());
    }
</script>

<Page>
    <!-- Top Navbar -->
    <Navbar title="Add room" backLink></Navbar>
    <Fab position="center-bottom" text="Save">
        <Icon md="material:done"></Icon>
      </Fab>
    <List noHairlinesMd>
      <ListInput
        outline
        label="Room name"
        floatingLabel
        type="text"
        placeholder="Bed room"
      ></ListInput>
    </List>
    <BlockTitle>Calibration nuovo</BlockTitle>
    <Block strong>
      <Button fill preloader loading={isLoading} onClick={saveReference}>
        Save reference
      </Button>
    </Block>
    <List mediaList>
      <ListItem
        title="Yellow Submarine"
        subtitle="Beatles">
        <img slot="media" src="https://cdn.framework7.io/placeholder/fashion-88x88-1.jpg" width="44" />
      </ListItem>
      <ListItem
        link="#"
        title="Don't Stop Me Now"
        subtitle="Queen">
        <img slot="media" src="https://cdn.framework7.io/placeholder/fashion-88x88-2.jpg" width="44" />
      </ListItem>
      <ListItem
        title="Billie Jean"
        subtitle="Michael Jackson">
        <img slot="media" src="https://cdn.framework7.io/placeholder/fashion-88x88-3.jpg" width="44" />
      </ListItem>
    </List>
</Page>

<style>
</style>