<script>
    import { Page, Navbar, Toolbar, 
    Link, Block, BlockTitle, 
    Row, Col, Button, NavRight, Toggle,
    ListItem, List, Card, CardContent, CardHeader } from 'framework7-svelte';
</script>

<Page>
    <!-- Top Navbar -->
    <Navbar title="Multiroom Audio">
      <NavRight>
        <Link  iconMd="material:leak_add" iconOnly href="/rooms/"></Link>
        <Link  iconMd="material:settings" iconOnly></Link>
      </NavRight>
    </Navbar>
    <!-- Toolbar -->
    <Toolbar bottom>
        <Link  iconMd="material:play_arrow" iconOnly></Link>
    </Toolbar>
    <BlockTitle>Connected speakers</BlockTitle>
    <div class="container">
      <div class="item">
        <Link iconMd="material:add" iconOnly></Link>
      </div>
      <div class="item"></div>
      <div class="item"></div>
      <div class="item"></div>
      <div class="item"></div>
      <div class="item"></div>
    </div>
    <BlockTitle>SLAC song list</BlockTitle>
    <List medial-list>
      <ListItem
        title="Yellow Submarine"
        subtitle="Beatles"
      >
        <img slot="media" alt="" src="https://cdn.framework7.io/placeholder/fashion-88x88-4.jpg" width="44"/>
        <span slot="after">
          <Link  iconMd="material:play_arrow" iconOnly></Link>
        </span>
      </ListItem>
      <ListItem
        title="Don't Stop Me Now"
        subtitle="Queen"
      >
        <img slot="media" alt="" src="https://cdn.framework7.io/placeholder/fashion-88x88-5.jpg" width="44"/>
        <span slot="after">
          <Link  iconMd="material:play_arrow" iconOnly></Link>
        </span>
      </ListItem>
      <ListItem
        title="Billie Jean"
        subtitle="Michael Jackson"
      >
        <img slot="media" alt="" src="https://cdn.framework7.io/placeholder/fashion-88x88-6.jpg" width="44"/>
        <span slot="after">
          <Link  iconMd="material:play_arrow" iconOnly></Link>
        </span>
      </ListItem>
    </List>
</Page>

<style>
  .container {
    margin: 5px;
    height: 100px;
    white-space: nowrap;
    display: flex;
    flex-wrap: nowrap; 
    overflow: auto;
  }
  .item {
    border: 1px solid rgba(0, 0, 0, 0.247);
    width: 100px;
    flex: 0 0 auto;
    margin-left: 10px;
    display: flex;
    align-items: center;
    justify-content: center
  }

</style>