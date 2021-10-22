<script>
    import { Page, Navbar, Fab, Icon, List, ListInput, 
              BlockTitle, Block, Button } from 'framework7-svelte';
    
    let isLoading = false;

    function load(){
      isLoading = true;
    }
</script>

<Page>
    <!-- Top Navbar -->
    <Navbar title="Add room" backLink="Back"></Navbar>
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
    <BlockTitle>Calibration</BlockTitle>
    <Block strong>
      <Button fill preloader loading={isLoading} onClick={load}>
        Save reference
      </Button>
    </Block>
</Page>

<style>
</style>